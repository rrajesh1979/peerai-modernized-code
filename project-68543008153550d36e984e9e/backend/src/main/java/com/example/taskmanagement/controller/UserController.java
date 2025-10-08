package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.dto.request.ChangePasswordRequest;
import com.example.taskmanagement.dto.request.CreateUserRequest;
import com.example.taskmanagement.dto.request.UpdateUserRequest;
import com.example.taskmanagement.dto.response.ApiResponse;
import com.example.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing User entities.
 * Provides endpoints for CRUD operations and user management.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param request the user creation request
     * @return the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create User: {}", request.getEmail());
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", createdUser));
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param request the user update request
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("REST request to update User: {}", id);
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    /**
     * Changes a user's password.
     *
     * @param id the ID of the user
     * @param request the password change request
     * @return success response
     */
    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("REST request to change password for User: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Gets a user by ID.
     *
     * @param id the ID of the user
     * @return the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        log.info("REST request to get User: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
    }

    /**
     * Gets the current authenticated user.
     *
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        log.info("REST request to get current User");
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(new ApiResponse<>(true, "Current user retrieved successfully", currentUser));
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        log.info("REST request to get all Users");
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Gets users by organization ID.
     *
     * @param organizationId the organization ID
     * @return list of users
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN') or @organizationSecurity.hasAccess(#organizationId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrganization(@PathVariable String organizationId) {
        log.info("REST request to get Users by Organization: {}", organizationId);
        List<UserDTO> users = userService.getUsersByOrganization(organizationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Gets users by project ID.
     *
     * @param projectId the project ID
     * @return list of users
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.hasAccess(#projectId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByProject(@PathVariable String projectId) {
        log.info("REST request to get Users by Project: {}", projectId);
        List<UserDTO> users = userService.getUsersByProject(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Deactivates a user.
     *
     * @param id the ID of the user to deactivate
     * @return success response
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable String id) {
        log.info("REST request to deactivate User: {}", id);
        userService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", null));
    }

    /**
     * Activates a user.
     *
     * @param id the ID of the user to activate
     * @return success response
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable String id) {
        log.info("REST request to activate User: {}", id);
        userService.activateUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User activated successfully", null));
    }

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.info("REST request to delete User: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }
}