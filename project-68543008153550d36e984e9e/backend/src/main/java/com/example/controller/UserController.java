package com.example.controller;

import com.example.dto.UserDTO;
import com.example.dto.UserCreateDTO;
import com.example.dto.UserUpdateDTO;
import com.example.service.UserService;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.ValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing User resources.
 * Provides endpoints for CRUD operations and user management functionality.
 * 
 * This controller implements role-based access control and comprehensive
 * error handling for all user-related operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users with pagination and sorting support.
     * 
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param sortBy Field to sort by (default: createdAt)
     * @param sortDirection Sort direction (default: DESC)
     * @return Paginated list of users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        logger.info("Fetching users - page: {}, size: {}, sortBy: {}, direction: {}", 
                    page, size, sortBy, sortDirection);

        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UserDTO> userPage = userService.getAllUsers(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent());
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("hasNext", userPage.hasNext());
            response.put("hasPrevious", userPage.hasPrevious());
            
            logger.info("Successfully retrieved {} users", userPage.getContent().size());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sort direction: {}", sortDirection, e);
            throw new ValidationException("Invalid sort direction. Use ASC or DESC");
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    /**
     * Retrieves a specific user by ID.
     * 
     * @param id User ID
     * @return User details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable @NotBlank String id) {
        logger.info("Fetching user with id: {}", id);
        
        try {
            UserDTO user = userService.getUserById(id);
            logger.info("Successfully retrieved user: {}", id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    /**
     * Retrieves a user by email address.
     * 
     * @param email User email
     * @return User details
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable @NotBlank String email) {
        logger.info("Fetching user with email: {}", email);
        
        try {
            UserDTO user = userService.getUserByEmail(email);
            logger.info("Successfully retrieved user by email: {}", email);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found with email: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with email: {}", email, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    /**
     * Retrieves a user by username.
     * 
     * @param username Username
     * @return User details
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable @NotBlank String username) {
        logger.info("Fetching user with username: {}", username);
        
        try {
            UserDTO user = userService.getUserByUsername(username);
            logger.info("Successfully retrieved user by username: {}", username);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found with username: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with username: {}", username, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    /**
     * Creates a new user.
     * 
     * @param userCreateDTO User creation data
     * @return Created user details
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        logger.info("Creating new user with email: {}", userCreateDTO.getEmail());
        
        try {
            UserDTO createdUser = userService.createUser(userCreateDTO);
            logger.info("Successfully created user with id: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (ValidationException e) {
            logger.warn("Validation error while creating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    /**
     * Updates an existing user.
     * 
     * @param id User ID
     * @param userUpdateDTO User update data
     * @return Updated user details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        
        logger.info("Updating user with id: {}", id);
        
        try {
            UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
            logger.info("Successfully updated user with id: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for update with id: {}", id);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error while updating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /**
     * Partially updates a user (PATCH operation).
     * 
     * @param id User ID
     * @param updates Map of fields to update
     * @return Updated user details
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> partialUpdateUser(
            @PathVariable @NotBlank String id,
            @RequestBody Map<String, Object> updates) {
        
        logger.info("Partially updating user with id: {}", id);
        
        try {
            UserDTO updatedUser = userService.partialUpdateUser(id, updates);
            logger.info("Successfully partially updated user with id: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for partial update with id: {}", id);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error while partially updating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error partially updating user with id: {}", id, e);
            throw new RuntimeException("Failed to partially update user", e);
        }
    }

    /**
     * Deletes a user by ID.
     * 
     * @param id User ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotBlank String id) {
        logger.info("Deleting user with id: {}", id);
        
        try {
            userService.deleteUser(id);
            logger.info("Successfully deleted user with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for deletion with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Soft deletes a user (marks as inactive).
     * 
     * @param id User ID
     * @return Updated user details
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable @NotBlank String id) {
        logger.info("Deactivating user with id: {}", id);
        
        try {
            UserDTO deactivatedUser = userService.deactivateUser(id);
            logger.info("Successfully deactivated user with id: {}", id);
            return ResponseEntity.ok(deactivatedUser);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for deactivation with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deactivating user with id: {}", id, e);
            throw new RuntimeException("Failed to deactivate user", e);
        }
    }

    /**
     * Reactivates a previously deactivated user.
     * 
     * @param id User ID
     * @return Updated user details
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable @NotBlank String id) {
        logger.info("Activating user with id: {}", id);
        
        try {
            UserDTO activatedUser = userService.activateUser(id);
            logger.info("Successfully activated user with id: {}", id);
            return ResponseEntity.ok(activatedUser);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for activation with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error activating user with id: {}", id, e);
            throw new RuntimeException("Failed to activate user", e);
        }
    }

    /**
     * Searches users by various criteria.
     * 
     * @param query Search query
     * @param page Page number
     * @param size Page size
     * @return Paginated search results
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam @NotBlank String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("Searching users with query: {}", query);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> userPage = userService.searchUsers(query, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent());
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            
            logger.info("Search returned {} users", userPage.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching users with query: {}", query, e);
            throw new RuntimeException("Failed to search users", e);
        }
    }

    /**
     * Checks if an email is already in use.
     * 
     * @param email Email to check
     * @return Boolean indicating availability
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(
            @RequestParam @NotBlank String email) {
        
        logger.info("Checking email availability: {}", email);
        
        try {
            boolean available = userService.isEmailAvailable(email);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", available);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking email availability: {}", email, e);
            throw new RuntimeException("Failed to check email availability", e);
        }
    }

    /**
     * Checks if a username is already in use.
     * 
     * @param username Username to check
     * @return Boolean indicating availability
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(
            @RequestParam @NotBlank String username) {
        
        logger.info("Checking username availability: {}", username);
        
        try {
            boolean available = userService.isUsernameAvailable(username);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", available);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking username availability: {}", username, e);
            throw new RuntimeException("Failed to check username availability", e);
        }