package com.jsf.migration.service;

import com.jsf.migration.exception.AuthenticationException;
import com.jsf.migration.exception.ResourceNotFoundException;
import com.jsf.migration.exception.ValidationException;
import com.jsf.migration.model.Session;
import com.jsf.migration.model.User;
import com.jsf.migration.repository.SessionRepository;
import com.jsf.migration.repository.UserRepository;
import com.jsf.migration.dto.UserDTO;
import com.jsf.migration.dto.UserProfileDTO;
import com.jsf.migration.dto.PasswordChangeDTO;
import com.jsf.migration.security.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * Handles user creation, authentication, profile management, and session tracking.
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${session.expiration.minutes:60}")
    private int sessionExpirationMinutes;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                       SessionRepository sessionRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Registers a new user in the system.
     *
     * @param userDTO the user data transfer object containing registration information
     * @return the created user entity
     * @throws ValidationException if username or email already exists
     */
    @Transactional
    public User registerUser(UserDTO userDTO) {
        logger.debug("Registering new user with username: {}", userDTO.getUsername());
        
        // Validate unique username and email
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }
        
        // Create new user entity
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        
        // Set up profile information
        user.getProfile().setFirstName(userDTO.getFirstName());
        user.getProfile().setLastName(userDTO.getLastName());
        user.getProfile().setRole("USER"); // Default role
        user.getProfile().setLastLogin(null); // No login yet
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    /**
     * Authenticates a user and creates a new session.
     *
     * @param username the username
     * @param password the password
     * @param ipAddress the IP address of the client
     * @param userAgent the user agent of the client
     * @return the created session
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public Session authenticateUser(String username, String password, String ipAddress, String userAgent) {
        logger.debug("Authenticating user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Failed login attempt for user: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }
        
        // Update last login time
        user.getProfile().setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Create new session
        Session session = new Session();
        session.setUserId(user.getUserId());
        session.setToken(generateSessionToken());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusMinutes(sessionExpirationMinutes));
        
        Session savedSession = sessionRepository.save(session);
        logger.info("User authenticated successfully: {}", username);
        
        return savedSession;
    }
    
    /**
     * Validates a session token.
     *
     * @param token the session token to validate
     * @return the user associated with the session
     * @throws AuthenticationException if the session is invalid or expired
     */
    public User validateSession(String token) {
        logger.debug("Validating session token");
        
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Invalid session"));
        
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Expired session attempt: {}", session.getSessionId());
            throw new AuthenticationException("Session expired");
        }
        
        return userRepository.findById(session.getUserId())
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }
    
    /**
     * Logs out a user by invalidating their session.
     *
     * @param token the session token to invalidate
     */
    @Transactional
    public void logoutUser(String token) {
        logger.debug("Logging out user with token");
        
        sessionRepository.findByToken(token).ifPresent(session -> {
            sessionRepository.delete(session);
            logger.info("User logged out successfully: {}", session.getUserId());
        });
    }
    
    /**
     * Updates a user's profile information.
     *
     * @param userId the ID of the user to update
     * @param profileDTO the profile data to update
     * @return the updated user
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public User updateUserProfile(String userId, UserProfileDTO profileDTO) {
        logger.debug("Updating profile for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update profile fields
        user.getProfile().setFirstName(profileDTO.getFirstName());
        user.getProfile().setLastName(profileDTO.getLastName());
        
        // Only administrators can update roles
        if (profileDTO.getRole() != null && isAdministratorRole(profileDTO.getCurrentUserRole())) {
            user.getProfile().setRole(profileDTO.getRole());
        }
        
        User updatedUser = userRepository.save(user);
        logger.info("User profile updated successfully: {}", userId);
        
        return updatedUser;
    }
    
    /**
     * Changes a user's password.
     *
     * @param userId the ID of the user
     * @param passwordChangeDTO the password change data
     * @throws AuthenticationException if the current password is incorrect
     */
    @Transactional
    public void changePassword(String userId, PasswordChangeDTO passwordChangeDTO) {
        logger.debug("Changing password for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPasswordHash())) {
            logger.warn("Failed password change attempt for user: {}", userId);
            throw new AuthenticationException("Current password is incorrect");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
        
        // Invalidate all existing sessions for security
        sessionRepository.deleteAllByUserId(userId);
        
        logger.info("Password changed successfully for user: {}", userId);
    }
    
    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    public User getUserById(String userId) {
        logger.debug("Retrieving user by ID: {}", userId);
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    /**
     * Retrieves a user by their username.
     *
     * @param username the username
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    public User getUserByUsername(String username) {
        logger.debug("Retrieving user by username: {}", username);
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    /**
     * Retrieves all users in the system.
     * This method should be restricted to administrative users.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        logger.debug("Retrieving all users");
        
        return userRepository.findAll();
    }
    
    /**
     * Deletes a user account.
     * This operation is irreversible and should be used with caution.
     *
     * @param userId the ID of the user to delete
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(String userId) {
        logger.debug("Deleting user: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        
        // Delete all sessions for the user
        sessionRepository.deleteAllByUserId(userId);
        
        // Delete the user
        userRepository.deleteById(userId);
        
        logger.info("User deleted successfully: {}", userId);
    }
    
    /**
     * Searches for users based on criteria.
     *
     * @param query the search query
     * @return a list of matching users
     */
    public List<UserDTO> searchUsers(String query) {
        logger.debug("Searching users with query: {}", query);
        
        List<User> users = userRepository.findByUsernameContainingOrEmailContainingOrProfileFirstNameContainingOrProfileLastNameContaining(
                query, query, query, query);
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if a user has administrative privileges.
     *
     * @param role the role to check
     * @return true if the role has administrative privileges
     */
    private boolean isAdministratorRole(String role) {
        return "ADMIN".equals(role) || "SUPER_ADMIN".equals(role);
    }
    
    /**
     * Generates a unique session token.
     *
     * @return a unique session token
     */
    private String generateSessionToken() {
        // In a production environment, use a more secure token generation method
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the user entity
     * @return the user DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getProfile().getFirstName());
        dto.setLastName(user.getProfile().getLastName());
        dto.setRole(user.getProfile().getRole());
        return dto;
    }
}