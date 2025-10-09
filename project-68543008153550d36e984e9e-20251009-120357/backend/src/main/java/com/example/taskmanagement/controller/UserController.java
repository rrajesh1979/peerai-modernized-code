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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing User entities.
 * Provides endpoints for user CRUD operations and profile management.
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
        log.info("Creating new user with username: {}", request.getUsername());
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", createdUser));
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("Retrieving all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user with the specified ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        log.info("Retrieving user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
    }

    /**
     * Updates a user.
     *
     * @param id the user ID
     * @param request the user update request
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    /**
     * Deletes a user.
     *
     * @param id the user ID
     * @return response indicating success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    /**
     * Changes a user's password.
     *
     * @param id the user ID
     * @param request the password change request
     * @return response indicating success
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Changing password for user with ID: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Retrieves the current authenticated user's profile.
     *
     * @return the current user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        log.info("Retrieving current user profile");
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(new ApiResponse<>(true, "Current user retrieved successfully", currentUser));
    }

    /**
     * Retrieves users by organization ID.
     *
     * @param organizationId the organization ID
     * @return list of users in the specified organization
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("@securityService.hasOrganizationAccess(#organizationId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrganization(@PathVariable String organizationId) {
        log.info("Retrieving users for organization with ID: {}", organizationId);
        List<UserDTO> users = userService.getUsersByOrganization(organizationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Organization users retrieved successfully", users));
    }

    /**
     * Retrieves users assigned to a specific project.
     *
     * @param projectId the project ID
     * @return list of users assigned to the specified project
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("@securityService.hasProjectAccess(#projectId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByProject(@PathVariable String projectId) {
        log.info("Retrieving users for project with ID: {}", projectId);
        List<UserDTO> users = userService.getUsersByProject(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project users retrieved successfully", users));
    }
}