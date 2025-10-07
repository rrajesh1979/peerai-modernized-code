package com.example.service;

import com.example.dto.UserDTO;
import com.example.dto.UserRegistrationDTO;
import com.example.dto.UserUpdateDTO;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing user operations.
 * Handles business logic for user creation, retrieval, update, and deletion.
 * Implements security best practices including password encryption and validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user account with encrypted password.
     * Validates that email and username are unique before creation.
     *
     * @param registrationDTO User registration data
     * @return Created user DTO
     * @throws DuplicateResourceException if email or username already exists
     */
    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        log.info("Creating new user with email: {}", registrationDTO.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            log.warn("Attempt to create user with duplicate email: {}", registrationDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + registrationDTO.getEmail());
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            log.warn("Attempt to create user with duplicate username: {}", registrationDTO.getUsername());
            throw new DuplicateResourceException("Username already exists: " + registrationDTO.getUsername());
        }

        // Create user entity
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setUsername(registrationDTO.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setEmailVerified(false);

        // Set default role if not provided
        if (registrationDTO.getRole() != null) {
            user.setRole(registrationDTO.getRole());
        } else {
            user.setRole("USER");
        }

        User savedUser = userRepository.save(user);
        log.info("Successfully created user with id: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId User ID
     * @return User DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(String userId) {
        log.debug("Fetching user by id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
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
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
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
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
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
        log.debug("Fetching all users with pagination: {}", pageable);

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
        log.debug("Fetching all active users");

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
        log.debug("Fetching users by role: {}", role);

        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's information.
     * Validates uniqueness of email and username if they are being changed.
     *
     * @param userId User ID
     * @param updateDTO User update data
     * @return Updated user DTO
     * @throws ResourceNotFoundException if user not found
     * @throws DuplicateResourceException if email or username already exists
     */
    public UserDTO updateUser(String userId, UserUpdateDTO updateDTO) {
        log.info("Updating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        // Validate email uniqueness if changed
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                log.warn("Attempt to update user with duplicate email: {}", updateDTO.getEmail());
                throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        // Validate username uniqueness if changed
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDTO.getUsername())) {
                log.warn("Attempt to update user with duplicate username: {}", updateDTO.getUsername());
                throw new DuplicateResourceException("Username already exists: " + updateDTO.getUsername());
            }
            user.setUsername(updateDTO.getUsername());
        }

        // Update other fields if provided
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }

        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }

        if (updateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }

        if (updateDTO.getActive() != null) {
            user.setActive(updateDTO.getActive());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user with id: {}", userId);

        return convertToDTO(updatedUser);
    }

    /**
     * Updates a user's password.
     * Encrypts the new password before storing.
     *
     * @param userId User ID
     * @param newPassword New password
     * @throws ResourceNotFoundException if user not found
     */
    public void updatePassword(String userId, String newPassword) {
        log.info("Updating password for user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Successfully updated password for user with id: {}", userId);
    }

    /**
     * Verifies a user's email address.
     *
     * @param userId User ID
     * @throws ResourceNotFoundException if user not found
     */
    public void verifyEmail(String userId) {
        log.info("Verifying email for user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Successfully verified email for user with id: {}", userId);
    }

    /**
     * Deactivates a user account.
     * Does not delete the user but marks them as inactive.
     *
     * @param userId User ID
     * @throws ResourceNotFoundException if user not found
     */
    public void deactivateUser(String userId) {
        log.info("Deactivating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Successfully deactivated user with id: {}", userId);
    }

    /**
     * Activates a previously deactivated user account.
     *
     * @param userId User ID
     * @throws ResourceNotFoundException if user not found
     */
    public void activateUser(String userId) {
        log.info("Activating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Successfully activated user with id: {}", userId);
    }

    /**
     * Permanently deletes a user account.
     * This operation cannot be undone.
     *
     * @param userId User ID
     * @throws ResourceNotFoundException if user not found
     */
    public void deleteUser(String userId) {
        log.info("Deleting user with id: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User not found with id: {}", userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("Successfully deleted user with id: {}", userId);
    }

    /**
     * Checks if a user exists by email.
     *
     * @param email Email address
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
     * Searches users by name (first name or last name).
     *
     * @param searchTerm Search term
     * @param pageable Pagination parameters
     * @return Page of matching user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsersByName(String searchTerm, Pageable pageable) {
        log.debug("Searching users by name: {}", searchTerm);

        Page<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm, pageable);

        return users.map(this::convertToDTO);
    }

    /**
     * Converts a User entity to a UserDTO.
     * Excludes sensitive information like password hash.
     *
     * @param user User entity
     * @return User DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());

        return dto;
    }

    /**
     * Updates the last login timestamp for a user.
     *
     * @param userId User ID
     */
    public void updateLastLogin(String userId) {
        log.debug("Updating last login for user with id: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLoginAt(LocalDateTime.now());