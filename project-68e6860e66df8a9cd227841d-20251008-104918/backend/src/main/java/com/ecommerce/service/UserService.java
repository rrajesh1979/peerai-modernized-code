package com.ecommerce.service;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.model.Profile;
import com.ecommerce.model.User;
import com.ecommerce.repository.ProfileRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-related operations.
 * Handles user registration, authentication, and profile management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user ID
     * @return The user entity
     * @throws ResourceNotFoundException if the user is not found
     */
    public User getUserById(String id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username
     * @return The user entity
     * @throws ResourceNotFoundException if the user is not found
     */
    public User getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email address
     * @return The user entity
     * @throws ResourceNotFoundException if the user is not found
     */
    public User getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable Pagination information
     * @return Page of users
     */
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    /**
     * Retrieves users by their role.
     *
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(String role) {
        log.debug("Fetching users with role: {}", role);
        return userRepository.findByRolesContaining(role);
    }

    /**
     * Creates a new user account.
     *
     * @param user The user entity to create
     * @return The created user
     * @throws UserAlreadyExistsException if a user with the same username or email already exists
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        
        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        
        // Set default values
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setLastUpdatedAt(LocalDateTime.now());
        
        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList("ROLE_USER"));
        }
        
        User savedUser = userRepository.save(user);
        
        // Log the user creation
        auditLogService.logUserAction(savedUser.getId(), "USER_CREATED", "User", savedUser.getId(), null, savedUser);
        
        return savedUser;
    }

    /**
     * Updates an existing user.
     *
     * @param id The user ID
     * @param userDetails The updated user details
     * @return The updated user
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public User updateUser(String id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User existingUser = getUserById(id);
        User beforeUpdate = new User(existingUser);
        
        // Update fields
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setStatus(userDetails.getStatus());
        existingUser.setRoles(userDetails.getRoles());
        existingUser.setLastUpdatedAt(LocalDateTime.now());
        
        // Only update password if provided
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        
        User updatedUser = userRepository.save(existingUser);
        
        // Log the user update
        auditLogService.logUserAction(updatedUser.getId(), "USER_UPDATED", "User", updatedUser.getId(), beforeUpdate, updatedUser);
        
        return updatedUser;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);
        
        User user = getUserById(id);
        
        // Delete associated profile if exists
        profileRepository.findByUserId(id).ifPresent(profile -> {
            profileRepository.delete(profile);
            auditLogService.logUserAction(id, "PROFILE_DELETED", "Profile", profile.getId(), profile, null);
        });
        
        userRepository.delete(user);
        
        // Log the user deletion
        auditLogService.logUserAction(id, "USER_DELETED", "User", id, user, null);
    }

    /**
     * Updates the user's last login timestamp.
     *
     * @param id The user ID
     * @return The updated user
     */
    @Transactional
    public User updateLastLogin(String id) {
        log.debug("Updating last login for user with id: {}", id);
        
        User user = getUserById(id);
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Changes a user's password.
     *
     * @param id The user ID
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return The updated user
     * @throws IllegalArgumentException if the current password is incorrect
     */
    @Transactional
    public User changePassword(String id, String currentPassword, String newPassword) {
        log.info("Changing password for user with id: {}", id);
        
        User user = getUserById(id);
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setLastUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        // Log the password change
        auditLogService.logUserAction(id, "PASSWORD_CHANGED", "User", id, null, null);
        
        return updatedUser;
    }

    /**
     * Retrieves a user's profile.
     *
     * @param userId The user ID
     * @return The user's profile
     * @throws ResourceNotFoundException if the profile is not found
     */
    public Profile getUserProfile(String userId) {
        log.debug("Fetching profile for user with id: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id: " + userId));
    }

    /**
     * Creates or updates a user's profile.
     *
     * @param userId The user ID
     * @param profile The profile details
     * @return The created or updated profile
     */
    @Transactional
    public Profile saveUserProfile(String userId, Profile profile) {
        log.info("Saving profile for user with id: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        Optional<Profile> existingProfile = profileRepository.findByUserId(userId);
        
        if (existingProfile.isPresent()) {
            // Update existing profile
            Profile profileToUpdate = existingProfile.get();
            Profile beforeUpdate = new Profile(profileToUpdate);
            
            profileToUpdate.setAddress(profile.getAddress());
            profileToUpdate.setPhoneNumber(profile.getPhoneNumber());
            profileToUpdate.setDateOfBirth(profile.getDateOfBirth());
            profileToUpdate.setPreferences(profile.getPreferences());
            profileToUpdate.setLastUpdatedAt(LocalDateTime.now());
            
            Profile updatedProfile = profileRepository.save(profileToUpdate);
            
            // Log the profile update
            auditLogService.logUserAction(userId, "PROFILE_UPDATED", "Profile", updatedProfile.getId(), beforeUpdate, updatedProfile);
            
            return updatedProfile;
        } else {
            // Create new profile
            profile.setUserId(userId);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setLastUpdatedAt(LocalDateTime.now());
            
            Profile savedProfile = profileRepository.save(profile);
            
            // Log the profile creation
            auditLogService.logUserAction(userId, "PROFILE_CREATED", "Profile", savedProfile.getId(), null, savedProfile);
            
            return savedProfile;
        }
    }

    /**
     * Activates a user account.
     *
     * @param id The user ID
     * @return The updated user
     */
    @Transactional
    public User activateUser(String id) {
        log.info("Activating user with id: {}", id);
        
        User user = getUserById(id);
        User beforeUpdate = new User(user);
        
        user.setStatus("ACTIVE");
        user.setLastUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        // Log the user activation
        auditLogService.logUserAction(id, "USER_ACTIVATED", "User", id, beforeUpdate, updatedUser);
        
        return updatedUser;
    }

    /**
     * Deactivates a user account.
     *
     * @param id The user ID
     * @return The updated user
     */
    @Transactional
    public User deactivateUser(String id) {
        log.info("Deactivating user with id: {}", id);
        
        User user = getUserById(id);
        User beforeUpdate = new User(user);
        
        user.setStatus("INACTIVE");
        user.setLastUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        // Log the user deactivation
        auditLogService.logUserAction(id, "USER_DEACTIVATED", "User", id, beforeUpdate, updatedUser);
        
        return updatedUser;
    }

    /**
     * Checks if a username is available.
     *
     * @param username The username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        log.debug("Checking if username is available: {}", username);
        return !userRepository.findByUsername(username).isPresent();
    }

    /**
     * Checks if an email is available.
     *
     * @param email The email to check
     * @return true if the email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        log.debug("Checking if email is available: {}", email);
        return !userRepository.findByEmail(email).isPresent();
    }
}