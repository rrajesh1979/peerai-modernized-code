package com.example.migration.service;

import com.example.migration.exception.ResourceNotFoundException;
import com.example.migration.exception.UserAlreadyExistsException;
import com.example.migration.model.User;
import com.example.migration.model.Profile;
import com.example.migration.model.Session;
import com.example.migration.repository.UserRepository;
import com.example.migration.repository.ProfileRepository;
import com.example.migration.repository.SessionRepository;
import com.example.migration.security.PasswordEncoder;
import com.example.migration.dto.UserDTO;
import com.example.migration.dto.UserRegistrationDTO;
import com.example.migration.dto.UserUpdateDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * Handles user creation, retrieval, update, and deletion operations.
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            SessionRepository sessionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * @param registrationDTO the user registration data
     * @return the created user DTO
     * @throws UserAlreadyExistsException if a user with the same username or email already exists
     */
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        logger.info("Registering new user with username: {}", registrationDTO.getUsername());
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            logger.warn("Username already exists: {}", registrationDTO.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            logger.warn("Email already exists: {}", registrationDTO.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setRole("USER"); // Default role
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Create default profile for the user
        Profile profile = new Profile();
        profile.setUserId(savedUser.getId());
        profile.setBio("");
        profile.setAvatar("");
        profileRepository.save(profile);
        logger.info("Default profile created for user ID: {}", savedUser.getId());
        
        return convertToDTO(savedUser);
    }
    
    /**
     * Retrieves a user by their ID.
     * 
     * @param id the user ID
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    public UserDTO getUserById(String id) {
        logger.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        return convertToDTO(user);
    }
    
    /**
     * Retrieves a user by their username.
     * 
     * @param username the username
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    public UserDTO getUserByUsername(String username) {
        logger.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
        return convertToDTO(user);
    }
    
    /**
     * Retrieves a user by their email.
     * 
     * @param email the email
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    public UserDTO getUserByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        return convertToDTO(user);
    }
    
    /**
     * Retrieves all users with pagination.
     * 
     * @param pageable the pagination information
     * @return a page of user DTOs
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    /**
     * Updates an existing user.
     * 
     * @param id the user ID
     * @param updateDTO the user update data
     * @return the updated user DTO
     * @throws ResourceNotFoundException if the user is not found
     * @throws UserAlreadyExistsException if the updated email already exists for another user
     */
    @Transactional
    public UserDTO updateUser(String id, UserUpdateDTO updateDTO) {
        logger.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        // Check if email is being changed and if it already exists
        if (updateDTO.getEmail() != null && 
            !updateDTO.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(updateDTO.getEmail())) {
            logger.warn("Email already exists: {}", updateDTO.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // Update user fields if provided
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        
        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }
        
        if (updateDTO.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Deactivates a user account.
     * 
     * @param id the user ID
     * @return the deactivated user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO deactivateUser(String id) {
        logger.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        User deactivatedUser = userRepository.save(user);
        
        // Invalidate all active sessions for this user
        List<Session> activeSessions = sessionRepository.findByUserIdAndIsActiveTrue(id);
        for (Session session : activeSessions) {
            session.setIsActive(false);
            sessionRepository.save(session);
        }
        
        logger.info("User deactivated successfully with ID: {}", id);
        return convertToDTO(deactivatedUser);
    }
    
    /**
     * Reactivates a user account.
     * 
     * @param id the user ID
     * @return the reactivated user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO reactivateUser(String id) {
        logger.info("Reactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        User reactivatedUser = userRepository.save(user);
        logger.info("User reactivated successfully with ID: {}", id);
        
        return convertToDTO(reactivatedUser);
    }
    
    /**
     * Permanently deletes a user and all associated data.
     * This operation should be used with caution.
     * 
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(String id) {
        logger.warn("Permanently deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        
        // Delete associated profile
        profileRepository.deleteByUserId(id);
        
        // Delete associated sessions
        sessionRepository.deleteByUserId(id);
        
        // Delete user
        userRepository.deleteById(id);
        logger.info("User and associated data deleted successfully with ID: {}", id);
    }
    
    /**
     * Updates the last login time for a user.
     * 
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void updateLastLogin(String id) {
        logger.debug("Updating last login time for user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        logger.debug("Last login time updated for user with ID: {}", id);
    }
    
    /**
     * Finds users by role.
     * 
     * @param role the role to search for
     * @param pageable the pagination information
     * @return a page of user DTOs
     */
    public Page<UserDTO> findUsersByRole(String role, Pageable pageable) {
        logger.debug("Finding users with role: {}", role);
        return userRepository.findByRole(role, pageable).map(this::convertToDTO);
    }
    
    /**
     * Searches for users by name (first name or last name).
     * 
     * @param name the name to search for
     * @param pageable the pagination information
     * @return a page of user DTOs
     */
    public Page<UserDTO> searchUsersByName(String name, Pageable pageable) {
        logger.debug("Searching users by name: {}", name);
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Converts a User entity to a UserDTO.
     * 
     * @param user the user entity
     * @return the user DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}