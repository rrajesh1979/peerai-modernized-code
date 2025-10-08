package com.modernization.service;

import com.modernization.exception.ResourceNotFoundException;
import com.modernization.exception.UserAlreadyExistsException;
import com.modernization.model.User;
import com.modernization.model.Profile;
import com.modernization.repository.UserRepository;
import com.modernization.repository.ProfileRepository;
import com.modernization.dto.UserDTO;
import com.modernization.dto.UserRegistrationDTO;
import com.modernization.dto.UserUpdateDTO;
import com.modernization.security.PasswordEncoder;
import com.modernization.event.UserCreatedEvent;
import com.modernization.event.UserUpdatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * Handles user registration, retrieval, updates, and deletion.
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                       ProfileRepository profileRepository,
                       PasswordEncoder passwordEncoder,
                       ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
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
        user.setRoles(registrationDTO.getRoles() != null ? registrationDTO.getRoles() : List.of("USER"));
        user.setActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Create empty profile for the user
        Profile profile = new Profile();
        profile.setUserId(savedUser.getId());
        profileRepository.save(profile);
        logger.info("Created empty profile for user ID: {}", savedUser.getId());
        
        // Publish user created event
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser));
        
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
     * @param pageable pagination information
     * @return a page of user DTOs
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Updates an existing user.
     * 
     * @param id the user ID
     * @param updateDTO the user update data
     * @return the updated user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO updateUser(String id, UserUpdateDTO updateDTO) {
        logger.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        // Update user fields if provided
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            // Check if email is already used by another user
            if (userRepository.existsByEmailAndIdNot(updateDTO.getEmail(), id)) {
                logger.warn("Email already in use: {}", updateDTO.getEmail());
                throw new UserAlreadyExistsException("Email already in use");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        if (updateDTO.getRoles() != null) {
            user.setRoles(updateDTO.getRoles());
        }
        
        if (updateDTO.getActive() != null) {
            user.setActive(updateDTO.getActive());
        }
        
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        user.setUpdatedAt(new Date());
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        
        // Publish user updated event
        eventPublisher.publishEvent(new UserUpdatedEvent(updatedUser));
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Deletes a user by their ID.
     * 
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(String id) {
        logger.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.warn("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        
        // Delete associated profile
        profileRepository.deleteByUserId(id);
        
        // Delete user
        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }
    
    /**
     * Updates the last login timestamp for a user.
     * 
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void updateLastLogin(String id) {
        logger.debug("Updating last login for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
        
        user.setLastLogin(new Date());
        userRepository.save(user);
    }
    
    /**
     * Finds users by role.
     * 
     * @param role the role to search for
     * @return a list of user DTOs with the specified role
     */
    public List<UserDTO> findUsersByRole(String role) {
        logger.debug("Finding users with role: {}", role);
        List<User> users = userRepository.findByRolesContaining(role);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for users based on a search term that matches username, first name, or last name.
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of user DTOs matching the search criteria
     */
    public Page<UserDTO> searchUsers(String searchTerm, Pageable pageable) {
        logger.debug("Searching users with term: {}", searchTerm);
        return userRepository.findByUsernameContainingOrFirstNameContainingOrLastNameContaining(
                searchTerm, searchTerm, searchTerm, pageable)
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
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}