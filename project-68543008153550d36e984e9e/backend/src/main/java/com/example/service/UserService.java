package com.example.service;

import com.example.dto.UserDTO;
import com.example.dto.UserRegistrationDTO;
import com.example.dto.UserUpdateDTO;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for User management operations.
 * Handles business logic for user creation, retrieval, update, and deletion.
 * Implements caching and transaction management for optimal performance.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user account with validation and password encryption.
     *
     * @param registrationDTO User registration data
     * @return Created user DTO
     * @throws DuplicateResourceException if email or username already exists
     */
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        logger.info("Creating new user with email: {}", registrationDTO.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            logger.warn("Attempt to create user with duplicate email: {}", registrationDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + registrationDTO.getEmail());
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            logger.warn("Attempt to create user with duplicate username: {}", registrationDTO.getUsername());
            throw new DuplicateResourceException("Username already exists: " + registrationDTO.getUsername());
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setUsername(registrationDTO.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setRole(registrationDTO.getRole() != null ? registrationDTO.getRole() : "USER");
        user.setActive(true);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        logger.info("Successfully created user with ID: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id User ID
     * @return User DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        logger.debug("Retrieving user by ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email User email
     * @return User DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Cacheable(value = "users", key = "#email")
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        logger.debug("Retrieving user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username Username
     * @return User DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Cacheable(value = "users", key = "#username")
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        logger.debug("Retrieving user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        return convertToDTO(user);
    }

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable Pagination parameters
     * @return Page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.debug("Retrieving all users with pagination: {}", pageable);

        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToDTO);
    }

    /**
     * Retrieves all active users.
     *
     * @return List of active user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        logger.debug("Retrieving all active users");

        List<User> activeUsers = userRepository.findByActiveTrue();
        return activeUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves users by role.
     *
     * @param role User role
     * @return List of user DTOs with specified role
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        logger.debug("Retrieving users by role: {}", role);

        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's information.
     *
     * @param id User ID
     * @param updateDTO User update data
     * @return Updated user DTO
     * @throws ResourceNotFoundException if user not found
     * @throws DuplicateResourceException if email or username conflicts
     */
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(String id, UserUpdateDTO updateDTO) {
        logger.info("Updating user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Check email uniqueness if email is being updated
        if (StringUtils.hasText(updateDTO.getEmail()) && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                logger.warn("Attempt to update user with duplicate email: {}", updateDTO.getEmail());
                throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        // Check username uniqueness if username is being updated
        if (StringUtils.hasText(updateDTO.getUsername()) && !updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDTO.getUsername())) {
                logger.warn("Attempt to update user with duplicate username: {}", updateDTO.getUsername());
                throw new DuplicateResourceException("Username already exists: " + updateDTO.getUsername());
            }
            user.setUsername(updateDTO.getUsername());
        }

        // Update other fields if provided
        if (StringUtils.hasText(updateDTO.getFirstName())) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (StringUtils.hasText(updateDTO.getLastName())) {
            user.setLastName(updateDTO.getLastName());
        }
        if (StringUtils.hasText(updateDTO.getPhoneNumber())) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateDTO.getProfilePictureUrl());
        }
        if (updateDTO.getPreferences() != null) {
            user.setPreferences(updateDTO.getPreferences());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        logger.info("Successfully updated user with ID: {}", id);

        return convertToDTO(updatedUser);
    }

    /**
     * Updates a user's password.
     *
     * @param id User ID
     * @param newPassword New password (plain text)
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", allEntries = true)
    public void updatePassword(String id, String newPassword) {
        logger.info("Updating password for user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Successfully updated password for user with ID: {}", id);
    }

    /**
     * Activates or deactivates a user account.
     *
     * @param id User ID
     * @param active Active status
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", allEntries = true)
    public void setUserActiveStatus(String id, boolean active) {
        logger.info("Setting active status to {} for user with ID: {}", active, id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Successfully set active status for user with ID: {}", id);
    }

    /**
     * Verifies a user's email address.
     *
     * @param id User ID
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", allEntries = true)
    public void verifyEmail(String id) {
        logger.info("Verifying email for user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Successfully verified email for user with ID: {}", id);
    }

    /**
     * Updates a user's last login timestamp.
     *
     * @param id User ID
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", key = "#id")
    public void updateLastLogin(String id) {
        logger.debug("Updating last login for user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        user.setLastLogin(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Deletes a user account (soft delete by deactivating).
     *
     * @param id User ID
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(String id) {
        logger.info("Deleting user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        User user = userRepository.findById(new ObjectId(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Soft delete by deactivating the user
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("Successfully deleted (deactivated) user with ID: {}", id);
    }

    /**
     * Permanently deletes a user account from the database.
     *
     * @param id User ID
     * @throws ResourceNotFoundException if user not found
     */
    @CacheEvict(value = "users", allEntries = true)
    public void permanentlyDeleteUser(String id) {
        logger.warn("Permanently deleting user with ID: {}", id);

        if (!ObjectId.isValid(id)) {
            logger.error("Invalid ObjectId format: {}", id);
            throw new IllegalArgumentException("Invalid user ID format");
        }

        if (!userRepository.existsById(new ObjectId(id))) {
            logger.error("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(new ObjectId(id));
        logger.warn("Successfully permanently deleted user with ID: {}", id);
    }

    /**
     * Checks if a user exists by email.
     *
     * @param email User email
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a user exists by username.
     *
     * @param username Username
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user User entity
     * @return User DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId().toString());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setPreferences(user.getPreferences());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}