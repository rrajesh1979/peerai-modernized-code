package com.ecommerce.controller;

import com.ecommerce.dto.UserDTO;
import com.ecommerce.dto.UserProfileDTO;
import com.ecommerce.dto.request.PasswordChangeRequest;
import com.ecommerce.dto.request.UserRegistrationRequest;
import com.ecommerce.dto.request.UserUpdateRequest;
import com.ecommerce.dto.response.ApiResponse;
import com.ecommerce.dto.response.PaginatedResponse;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for user registration, profile management, and administrative functions.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user in the system.
     *
     * @param request Registration details for the new user
     * @return The created user information
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        UserDTO createdUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", createdUser));
    }

    /**
     * Retrieves the current authenticated user's information.
     *
     * @param principal The authenticated user principal
     * @return The current user's information
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Principal principal) {
        log.debug("Fetching current user information for: {}", principal.getName());
        UserDTO userDTO = userService.getUserByUsername(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userDTO));
    }

    /**
     * Retrieves the profile information for the current authenticated user.
     *
     * @param principal The authenticated user principal
     * @return The current user's profile information
     */
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUserProfile(Principal principal) {
        log.debug("Fetching profile for user: {}", principal.getName());
        UserProfileDTO profileDTO = userService.getUserProfileByUsername(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile retrieved successfully", profileDTO));
    }

    /**
     * Updates the current authenticated user's information.
     *
     * @param request   The user update request
     * @param principal The authenticated user principal
     * @return The updated user information
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request, Principal principal) {
        log.info("Updating user information for: {}", principal.getName());
        UserDTO updatedUser = userService.updateUser(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    /**
     * Changes the password for the current authenticated user.
     *
     * @param request   The password change request
     * @param principal The authenticated user principal
     * @return Success response
     */
    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request, Principal principal) {
        log.info("Changing password for user: {}", principal.getName());
        userService.changePassword(principal.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Retrieves a user by their ID. Admin access only.
     *
     * @param userId The ID of the user to retrieve
     * @return The requested user information
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        log.debug("Admin fetching user with ID: {}", userId);
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userDTO));
    }

    /**
     * Retrieves a paginated list of all users. Admin access only.
     *
     * @param pageable Pagination parameters
     * @return Paginated list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        log.debug("Admin fetching paginated list of users");
        PaginatedResponse<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Updates a user's information by their ID. Admin access only.
     *
     * @param userId  The ID of the user to update
     * @param request The user update request
     * @return The updated user information
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String userId, @Valid @RequestBody UserUpdateRequest request) {
        log.info("Admin updating user with ID: {}", userId);
        UserDTO updatedUser = userService.updateUserById(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    /**
     * Activates a user account. Admin access only.
     *
     * @param userId The ID of the user to activate
     * @return Success response
     */
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable String userId) {
        log.info("Admin activating user with ID: {}", userId);
        userService.activateUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User activated successfully", null));
    }

    /**
     * Deactivates a user account. Admin access only.
     *
     * @param userId The ID of the user to deactivate
     * @return Success response
     */
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable String userId) {
        log.info("Admin deactivating user with ID: {}", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", null));
    }

    /**
     * Searches for users based on criteria. Admin access only.
     *
     * @param query    The search query
     * @param pageable Pagination parameters
     * @return Paginated list of matching users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<UserDTO>> searchUsers(
            @RequestParam String query, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Admin searching for users with query: {}", query);
        PaginatedResponse<UserDTO> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Handles exceptions related to user operations.
     *
     * @param ex The exception that was thrown
     * @return Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleExceptions(Exception ex) {
        log.error("Error in UserController: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "An error occurred: " + ex.getMessage(), null));
    }
}