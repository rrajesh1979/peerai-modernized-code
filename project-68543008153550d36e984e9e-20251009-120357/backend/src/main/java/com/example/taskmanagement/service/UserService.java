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
 * Handles user creation, retrieval, update, and deletion operations.
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
     * @return List of UserDTO objects
     */
    @Transactional(readOnly = true)
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
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The user email
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Creates a new user.
     *
     * @param userDTO The user data
     * @return Created UserDTO object
     * @throws ResourceAlreadyExistsException if email or username already exists
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Creating new user with email: {}", userDTO.getEmail());
        
        // Check if email already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already in use: " + userDTO.getEmail());
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken: " + userDTO.getUsername());
        }
        
        User user = convertToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        
        // Encode password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id The user ID
     * @param userDTO The updated user data
     * @return Updated UserDTO object
     * @throws ResourceNotFoundException if user is not found
     * @throws ResourceAlreadyExistsException if email or username already exists
     */
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        log.debug("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Check if email is being changed and if it's already in use
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
                userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already in use: " + userDTO.getEmail());
        }
        
        // Check if username is being changed and if it's already taken
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
                userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken: " + userDTO.getUsername());
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
        log.info("User updated successfully with ID: {}", updatedUser.getId());
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
        log.debug("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param id The user ID
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    /**
     * Converts User entity to UserDTO.
     *
     * @param user The User entity
     * @return UserDTO object
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
     * Converts UserDTO to User entity.
     *
     * @param userDTO The UserDTO object
     * @return User entity
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