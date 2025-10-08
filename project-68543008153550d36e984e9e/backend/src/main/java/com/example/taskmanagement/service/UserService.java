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
 * Service class for handling user-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users from the database
     *
     * @return List of UserDTO objects
     */
    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID
     *
     * @param id The user ID
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserById(String id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their email
     *
     * @param email The user's email
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Retrieves a user by their username
     *
     * @param username The username
     * @return UserDTO object
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Creates a new user
     *
     * @param userDTO The user data
     * @return Created UserDTO
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
        
        // Set creation timestamp
        user.setCreatedAt(LocalDateTime.now());
        
        // Encode password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * Updates an existing user
     *
     * @param id The user ID
     * @param userDTO The updated user data
     * @return Updated UserDTO
     * @throws ResourceNotFoundException if user is not found
     * @throws ResourceAlreadyExistsException if email or username already exists
     */
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        log.debug("Updating user with id: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
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
        
        // Update password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Update other fields if they exist in the DTO
        Optional.ofNullable(userDTO.getFirstName()).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(userDTO.getLastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(userDTO.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);
        
        // Set update timestamp
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return convertToDTO(updatedUser);
    }

    /**
     * Deletes a user by their ID
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
     * Converts a User entity to UserDTO
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
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converts a UserDTO to User entity
     *
     * @param userDTO The UserDTO
     * @return User entity
     */
    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();
    }
    
    /**
     * Checks if a user exists by their ID
     *
     * @param id The user ID
     * @return true if user exists, false otherwise
     */
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Checks if a user exists by their email
     *
     * @param email The user email
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Checks if a user exists by their username
     *
     * @param username The username
     * @return true if user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}