package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.exception.ResourceAlreadyExistsException;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * Handles user creation, retrieval, updates, and deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users converted to DTOs
     */
    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user ID
     * @return The user DTO
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserById(String id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The user's email
     * @return The user DTO
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username
     * @return The user DTO
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }

    /**
     * Creates a new user.
     *
     * @param userDTO The user data
     * @return The created user DTO
     * @throws ResourceAlreadyExistsException if user with email or username already exists
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Creating new user with email: {}", userDTO.getEmail());
        
        // Check if email already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with username " + userDTO.getUsername() + " already exists");
        }
        
        User user = convertToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        
        // Encode password if present
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id The user ID
     * @param userDTO The updated user data
     * @return The updated user DTO
     * @throws ResourceNotFoundException if user is not found
     * @throws ResourceAlreadyExistsException if updated email or username conflicts with existing users
     */
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        log.debug("Updating user with id: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Check if email is being changed and if it conflicts
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
                userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists");
        }
        
        // Check if username is being changed and if it conflicts
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
                userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with username " + userDTO.getUsername() + " already exists");
        }
        
        // Update user fields
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Update password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return convertToDTO(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The user ID
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public void deleteUser(String id) {
        log.debug("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    /**
     * Checks if the provided credentials are valid.
     *
     * @param email The user's email
     * @param password The password to verify
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateCredentials(String email, String password) {
        log.debug("Validating credentials for user with email: {}", email);
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    /**
     * Changes a user's password.
     *
     * @param id The user ID
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return true if password was changed successfully, false otherwise
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public boolean changePassword(String id, String currentPassword, String newPassword) {
        log.debug("Changing password for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            log.warn("Password change failed - current password doesn't match for user id: {}", id);
            return false;
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password changed successfully for user id: {}", id);
        return true;
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity
     * @return The UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO The UserDTO
     * @return The User entity
     */
    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .build();
    }
}