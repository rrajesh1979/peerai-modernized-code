package com.modernization.repository;

import com.modernization.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations with MongoDB.
 * Provides methods for CRUD operations and custom queries related to user management.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username already exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by their active status.
     *
     * @param active the active status to filter by
     * @param pageable pagination information
     * @return a Page of users with the specified active status
     */
    Page<User> findByActive(boolean active, Pageable pageable);

    /**
     * Find users by role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    @Query("{ 'roles': ?0 }")
    List<User> findByRole(String role);

    /**
     * Find users who haven't logged in since the specified date.
     *
     * @param date the cutoff date
     * @return a list of users who haven't logged in since the specified date
     */
    List<User> findByLastLoginBefore(LocalDateTime date);

    /**
     * Find users by first name and last name.
     *
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return a list of users matching the specified first and last name
     */
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Search users by username, first name, or last name containing the search term.
     *
     * @param searchTerm the search term to look for
     * @param pageable pagination information
     * @return a Page of users matching the search criteria
     */
    @Query("{ $or: [ " +
           "{ 'username': { $regex: ?0, $options: 'i' } }, " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Count users by role.
     *
     * @param role the role to count
     * @return the number of users with the specified role
     */
    @Query(value = "{ 'roles': ?0 }", count = true)
    long countByRole(String role);

    /**
     * Deactivate users who haven't logged in since the specified date.
     *
     * @param date the cutoff date
     * @return the number of users deactivated
     */
    @Query(value = "{ 'lastLogin': { $lt: ?0 }, 'active': true }", 
           fields = "{ 'active': false }")
    int deactivateInactiveUsersSince(LocalDateTime date);
}