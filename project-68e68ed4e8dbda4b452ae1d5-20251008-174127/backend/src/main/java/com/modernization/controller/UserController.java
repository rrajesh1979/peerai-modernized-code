package com.modernization.controller;

import com.modernization.dto.UserCreateDTO;
import com.modernization.dto.UserDTO;
import com.modernization.dto.UserUpdateDTO;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userCreateDTO the user to create
     * @return the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.debug("REST request to create User: {}", userCreateDTO);
        try {
            UserDTO result = userService.createUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User creation failed: " + e.getMessage());
        }
    }

    /**
     * Updates an existing user.
     *
     * @param id the id of the user to update
     * @param userUpdateDTO the user to update
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.debug("REST request to update User: {}", userUpdateDTO);
        try {
            UserDTO result = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to update user with id {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User update failed: " + e.getMessage());
        }
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable the pagination information
     * @return the list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get all Users");
        Page<UserDTO> page = userService.getAllUsers(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Gets a user by id.
     *
     * @param id the id of the user to retrieve
     * @return the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        log.debug("REST request to get User: {}", id);
        try {
            UserDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.error("Failed to get user with id {}", id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + e.getMessage());
        }
    }

    /**
     * Deletes a user by id.
     *
     * @param id the id of the user to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.debug("REST request to delete User: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete user with id {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User deletion failed: " + e.getMessage());
        }
    }

    /**
     * Gets a user by username.
     *
     * @param username the username of the user to retrieve
     * @return the user
     */
    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUserByUsername(#username)")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.debug("REST request to get User by username: {}", username);
        try {
            UserDTO userDTO = userService.getUserByUsername(username);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.error("Failed to get user with username {}", username, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + e.getMessage());
        }
    }

    /**
     * Activates a user.
     *
     * @param id the id of the user to activate
     * @return the activated user
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable String id) {
        log.debug("REST request to activate User: {}", id);
        try {
            UserDTO result = userService.activateUser(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to activate user with id {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User activation failed: " + e.getMessage());
        }
    }

    /**
     * Deactivates a user.
     *
     * @param id the id of the user to deactivate
     * @return the deactivated user
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable String id) {
        log.debug("REST request to deactivate User: {}", id);
        try {
            UserDTO result = userService.deactivateUser(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to deactivate user with id {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User deactivation failed: " + e.getMessage());
        }
    }

    /**
     * Gets users by role.
     *
     * @param role the role to filter by
     * @return the list of users with the specified role
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        log.debug("REST request to get Users by role: {}", role);
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}