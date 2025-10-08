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
 * Provides endpoints for CRUD operations and user-specific functionality.
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
     * @return the created user DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create User: {}", request.getEmail());
        UserDTO result = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", result));
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param request the user update request
     * @return the updated user DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("REST request to update User: {}", id);
        UserDTO result = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", result));
    }

    /**
     * Changes a user's password.
     *
     * @param id the ID of the user
     * @param request the password change request
     * @return success response
     */
    @PutMapping("/{id}/change-password")
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("REST request to change password for User: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Gets a user by ID.
     *
     * @param id the ID of the user
     * @return the user DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String id) {
        log.info("REST request to get User: {}", id);
        UserDTO result = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", result));
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return a page of user DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        log.info("REST request to get all Users");
        Page<UserDTO> result = userService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", result));
    }

    /**
     * Gets users by organization ID.
     *
     * @param organizationId the organization ID
     * @return list of user DTOs
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrganization(@PathVariable String organizationId) {
        log.info("REST request to get Users by Organization: {}", organizationId);
        List<UserDTO> result = userService.getUsersByOrganization(organizationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", result));
    }

    /**
     * Gets users by project ID.
     *
     * @param projectId the project ID
     * @return list of user DTOs
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @projectSecurity.isProjectMember(#projectId)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByProject(@PathVariable String projectId) {
        log.info("REST request to get Users by Project: {}", projectId);
        List<UserDTO> result = userService.getUsersByProject(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", result));
    }

    /**
     * Deactivates a user account.
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
     * Activates a user account.
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