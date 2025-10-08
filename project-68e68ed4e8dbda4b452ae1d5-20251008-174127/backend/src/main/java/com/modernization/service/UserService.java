package com.modernization.service;

import com.modernization.exception.ResourceNotFoundException;
import com.modernization.exception.UserAlreadyExistsException;
import com.modernization.model.Profile;
import com.modernization.model.User;
import com.modernization.repository.ProfileRepository;
import com.modernization.repository.UserRepository;
import com.modernization.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user-related operations.
 * Handles user creation, retrieval, updates, and deletion.
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
     * Retrieves all users with pagination support.
     *
     * @param pageable Pagination information
     * @return Page of users
     */
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Retrieving all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id User ID
     * @return User object
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserById(String id) {
        log.debug("Retrieving user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username Username to search for
     * @return User object
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserByUsername(String username) {
        log.debug("Retrieving user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email Email to search for
     * @return User object
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserByEmail(String email) {
        log.debug("Retrieving user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Creates a new user with validation for existing username/email.
     *
     * @param user User to create
     * @return Created user
     * @throws UserAlreadyExistsException if username or email already exists
     */
    @Transactional
    public User createUser(User user) {
        log.debug("Creating new user with username: {}", user.getUsername());
        
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        
        // Hash password before storing
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Create empty profile for the user
        Profile profile = new Profile();
        profile.setUserId(savedUser.getId());
        profileRepository.save(profile);
        
        // Log the user creation
        auditLogService.logUserAction(savedUser.getId(), "USER_CREATED", "User", savedUser.getId(), null);
        
        return savedUser;
    }

    /**
     * Updates an existing user.
     *
     * @param id User ID
     * @param userDetails Updated user details
     * @return Updated user
     * @throws ResourceNotFoundException if user not found
     * @throws UserAlreadyExistsException if updated email/username conflicts with existing user
     */
    @Transactional
    public User updateUser(String id, User userDetails) {
        log.debug("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Check if username is being changed and if it conflicts
        if (!existingUser.getUsername().equals(userDetails.getUsername()) &&
                userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + userDetails.getUsername());
        }
        
        // Check if email is being changed and if it conflicts
        if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + userDetails.getEmail());
        }
        
        // Update user fields
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setRoles(userDetails.getRoles());
        existingUser.setActive(userDetails.isActive());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Only update password if provided
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        
        User updatedUser = userRepository.save(existingUser);
        
        // Log the user update
        auditLogService.logUserAction(updatedUser.getId(), "USER_UPDATED", "User", updatedUser.getId(), null);
        
        return updatedUser;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id User ID to delete
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void deleteUser(String id) {
        log.debug("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Delete associated profile
        profileRepository.findByUserId(id).ifPresent(profileRepository::delete);
        
        // Delete the user
        userRepository.delete(user);
        
        // Log the user deletion
        auditLogService.logUserAction(id, "USER_DELETED", "User", id, null);
    }

    /**
     * Updates the last login time for a user.
     *
     * @param username Username of the user
     */
    @Transactional
    public void updateLastLogin(String username) {
        log.debug("Updating last login time for user: {}", username);
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Log the login
            auditLogService.logUserAction(user.getId(), "USER_LOGIN", "User", user.getId(), null);
        } else {
            log.warn("Attempted to update last login for non-existent user: {}", username);
        }
    }

    /**
     * Changes a user's password.
     *
     * @param id User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return Updated user
     * @throws ResourceNotFoundException if user not found
     * @throws IllegalArgumentException if current password is incorrect
     */
    @Transactional
    public User changePassword(String id, String currentPassword, String newPassword) {
        log.debug("Changing password for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        // Log the password change
        auditLogService.logUserAction(user.getId(), "PASSWORD_CHANGED", "User", user.getId(), null);
        
        return updatedUser;
    }

    /**
     * Finds users by role.
     *
     * @param role Role to search for
     * @return List of users with the specified role
     */
    public List<User> findUsersByRole(String role) {
        log.debug("Finding users with role: {}", role);
        return userRepository.findByRolesContaining(role);
    }

    /**
     * Activates or deactivates a user account.
     *
     * @param id User ID
     * @param active Active status to set
     * @return Updated user
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public User setUserActiveStatus(String id, boolean active) {
        log.debug("Setting active status to {} for user ID: {}", active, id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        // Log the status change
        String action = active ? "USER_ACTIVATED" : "USER_DEACTIVATED";
        auditLogService.logUserAction(user.getId(), action, "User", user.getId(), null);
        
        return updatedUser;
    }

    /**
     * Searches for users based on a search term (matches against username, email, first name, or last name).
     *
     * @param searchTerm Term to search for
     * @param pageable Pagination information
     * @return Page of matching users
     */
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, searchTerm, pageable);
    }
}