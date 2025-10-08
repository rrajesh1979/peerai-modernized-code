package com.example.migration.controller;

import com.example.migration.dto.UserCreateDto;
import com.example.migration.dto.UserResponseDto;
import com.example.migration.dto.UserUpdateDto;
import com.example.migration.service.UserService;
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
     * @param userCreateDto the user to create
     * @return the created user with status 201
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.debug("REST request to create User: {}", userCreateDto);
        UserResponseDto createdUser = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param userUpdateDto the user data to update
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.debug("REST request to update User: {}", id);
        UserResponseDto updatedUser = userService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Gets a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String id) {
        log.debug("REST request to get User: {}", id);
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return a page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get all Users");
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
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
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable String role) {
        log.debug("REST request to get Users by role: {}", role);
        List<UserResponseDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Activates or deactivates a user.
     *
     * @param id the ID of the user
     * @param active the active status to set
     * @return the updated user
     */
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> setUserActiveStatus(
            @PathVariable String id,
            @RequestParam boolean active) {
        log.debug("REST request to set User active status: {} to {}", id, active);
        UserResponseDto user = userService.setUserActiveStatus(id, active);
        return ResponseEntity.ok(user);
    }

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.debug("REST request to delete User: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets the current authenticated user.
     *
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.debug("REST request to get current User");
        UserResponseDto currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Searches for users by username or email.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return a page of matching users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        log.debug("REST request to search Users with query: {}", query);
        Page<UserResponseDto> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }
}