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
 * Provides endpoints for CRUD operations and user-specific functionalities.
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
     * @param request The user creation request
     * @return ResponseEntity containing the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create user with username: {}", request.getUsername());
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", createdUser));
    }

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable Pagination information
     * @return ResponseEntity containing a page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        log.info("REST request to get all users");
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The user ID
     * @return ResponseEntity containing the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        log.info("REST request to get user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
    }

    /**
     * Updates a user.
     *
     * @param id The user ID
     * @param request The user update request
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("REST request to update user with ID: {}", id);
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    /**
     * Deletes a user.
     *
     * @param id The user ID
     * @return ResponseEntity with deletion confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.info("REST request to delete user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    /**
     * Changes a user's password.
     *
     * @param id The user ID
     * @param request The password change request
     * @return ResponseEntity with confirmation message
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("REST request to change password for user with ID: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Retrieves users by organization ID.
     *
     * @param organizationId The organization ID
     * @return ResponseEntity containing a list of users
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN') or @organizationSecurity.hasAccess(#organizationId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrganization(@PathVariable String organizationId) {
        log.info("REST request to get users for organization with ID: {}", organizationId);
        List<UserDTO> users = userService.getUsersByOrganization(organizationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Retrieves users assigned to a specific project.
     *
     * @param projectId The project ID
     * @return ResponseEntity containing a list of users
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.hasAccess(#projectId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByProject(@PathVariable String projectId) {
        log.info("REST request to get users for project with ID: {}", projectId);
        List<UserDTO> users = userService.getUsersByProject(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Searches for users based on search criteria.
     *
     * @param query The search query
     * @param pageable Pagination information
     * @return ResponseEntity containing a page of users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        log.info("REST request to search users with query: {}", query);
        Page<UserDTO> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }
}