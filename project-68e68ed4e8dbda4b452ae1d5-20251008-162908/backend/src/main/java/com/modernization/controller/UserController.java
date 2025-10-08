package com.modernization.controller;

import com.modernization.dto.UserCreateRequest;
import com.modernization.dto.UserResponse;
import com.modernization.dto.UserUpdateRequest;
import com.modernization.model.User;
import com.modernization.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing User resources.
 * Provides endpoints for CRUD operations on users.
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
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("REST request to create User: {}", request.getUsername());
        try {
            UserResponse createdUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating user: " + e.getMessage());
        }
    }

    /**
     * Updates an existing user.
     *
     * @param id the id of the user to update
     * @param request the user update request
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("REST request to update User: {}", id);
        try {
            UserResponse updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating user: " + e.getMessage());
        }
    }

    /**
     * Gets a user by id.
     *
     * @param id the id of the user to retrieve
     * @return the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("REST request to get User: {}", id);
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to get user with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return a page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("REST request to get all Users");
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Gets users by role.
     *
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        log.info("REST request to get Users by role: {}", role);
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Deactivates a user.
     *
     * @param id the id of the user to deactivate
     * @return no content response
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable String id) {
        log.info("REST request to deactivate User: {}", id);
        try {
            userService.deactivateUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to deactivate user with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deactivating user: " + e.getMessage());
        }
    }

    /**
     * Activates a user.
     *
     * @param id the id of the user to activate
     * @return no content response
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable String id) {
        log.info("REST request to activate User: {}", id);
        try {
            userService.activateUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to activate user with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error activating user: " + e.getMessage());
        }
    }

    /**
     * Deletes a user.
     *
     * @param id the id of the user to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("REST request to delete User: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete user with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting user: " + e.getMessage());
        }
    }

    /**
     * Gets the current authenticated user.
     *
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.info("REST request to get current User");
        try {
            UserResponse currentUser = userService.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error retrieving current user");
        }
    }
}