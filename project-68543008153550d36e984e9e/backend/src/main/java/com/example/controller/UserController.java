package com.example.controller;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.dto.UserUpdateDTO;
import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing user operations.
 * Provides endpoints for CRUD operations on users with proper security and validation.
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user.
     *
     * @param userRequestDTO the user data to create
     * @return ResponseEntity containing the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Creating new user with email: {}", userRequestDTO.getEmail());
        
        try {
            User createdUser = userService.createUser(userRequestDTO);
            UserResponseDTO responseDTO = convertToResponseDTO(createdUser);
            
            logger.info("Successfully created user with ID: {}", createdUser.getId());
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating user with email: {}", userRequestDTO.getEmail(), e);
            throw e;
        }
    }

    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return ResponseEntity containing the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        logger.info("Fetching user with ID: {}", id);
        
        try {
            User user = userService.getUserById(id);
            UserResponseDTO responseDTO = convertToResponseDTO(user);
            
            logger.debug("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Get a user by email.
     *
     * @param email the user email
     * @return ResponseEntity containing the user
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        
        try {
            User user = userService.getUserByEmail(email);
            UserResponseDTO responseDTO = convertToResponseDTO(user);
            
            logger.debug("Successfully retrieved user with email: {}", email);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error fetching user with email: {}", email, e);
            throw e;
        }
    }

    /**
     * Get a user by username.
     *
     * @param username the username
     * @return ResponseEntity containing the user
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        logger.info("Fetching user with username: {}", username);
        
        try {
            User user = userService.getUserByUsername(username);
            UserResponseDTO responseDTO = convertToResponseDTO(user);
            
            logger.debug("Successfully retrieved user with username: {}", username);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error fetching user with username: {}", username, e);
            throw e;
        }
    }

    /**
     * Get all users with pagination and sorting.
     *
     * @param page the page number (default: 0)
     * @param size the page size (default: 10)
     * @param sortBy the field to sort by (default: createdAt)
     * @param sortDir the sort direction (default: desc)
     * @return ResponseEntity containing paginated users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("Fetching all users - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<User> userPage = userService.getAllUsers(pageable);
            List<UserResponseDTO> users = userPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            
            logger.debug("Successfully retrieved {} users", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw e;
        }
    }

    /**
     * Update a user.
     *
     * @param id the user ID
     * @param userUpdateDTO the updated user data
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        
        logger.info("Updating user with ID: {}", id);
        
        try {
            User updatedUser = userService.updateUser(id, userUpdateDTO);
            UserResponseDTO responseDTO = convertToResponseDTO(updatedUser);
            
            logger.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Partially update a user.
     *
     * @param id the user ID
     * @param updates the fields to update
     * @return ResponseEntity containing the updated user
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<UserResponseDTO> partialUpdateUser(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        
        logger.info("Partially updating user with ID: {}", id);
        
        try {
            User updatedUser = userService.partialUpdateUser(id, updates);
            UserResponseDTO responseDTO = convertToResponseDTO(updatedUser);
            
            logger.info("Successfully partially updated user with ID: {}", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error partially updating user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Delete a user.
     *
     * @param id the user ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("Deleting user with ID: {}", id);
        
        try {
            userService.deleteUser(id);
            logger.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Activate a user account.
     *
     * @param id the user ID
     * @return ResponseEntity containing the activated user
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable String id) {
        logger.info("Activating user with ID: {}", id);
        
        try {
            User activatedUser = userService.activateUser(id);
            UserResponseDTO responseDTO = convertToResponseDTO(activatedUser);
            
            logger.info("Successfully activated user with ID: {}", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error activating user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Deactivate a user account.
     *
     * @param id the user ID
     * @return ResponseEntity containing the deactivated user
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable String id) {
        logger.info("Deactivating user with ID: {}", id);
        
        try {
            User deactivatedUser = userService.deactivateUser(id);
            UserResponseDTO responseDTO = convertToResponseDTO(deactivatedUser);
            
            logger.info("Successfully deactivated user with ID: {}", id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error deactivating user with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Search users by criteria.
     *
     * @param searchTerm the search term
     * @param page the page number
     * @param size the page size
     * @return ResponseEntity containing search results
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Searching users with term: {}", searchTerm);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userService.searchUsers(searchTerm, pageable);
            
            List<UserResponseDTO> users = userPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            
            logger.debug("Found {} users matching search term: {}", users.size(), searchTerm);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching users with term: {}", searchTerm, e);
            throw e;
        }
    }

    /**
     * Get users by organization.
     *
     * @param organizationId the organization ID
     * @param page the page number
     * @param size the page size
     * @return ResponseEntity containing users in the organization
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUsersByOrganization(
            @PathVariable String organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Fetching users for organization ID: {}", organizationId);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userService.getUsersByOrganization(organizationId, pageable);
            
            List<UserResponseDTO> users = userPage.getContent().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            
            logger.debug("Found {} users for organization ID: {}", users.size(), organizationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching users for organization ID: {}", organizationId, e);
            throw e;
        }
    }

    /**
     * Convert User entity to UserResponseDTO.
     * Excludes sensitive information like password hash.
     *
     * @param user the user entity
     * @return UserResponseDTO
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setRoles(user.getRoles());
        dto.setOrganizationIds(user.getOrganizationIds());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}