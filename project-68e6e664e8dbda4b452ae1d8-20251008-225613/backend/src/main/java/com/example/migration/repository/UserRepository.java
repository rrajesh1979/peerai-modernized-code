package com.example.migration.repository;

import com.example.migration.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods to interact with the Users collection in MongoDB.
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
     * Find a user by either username or email.
     *
     * @param username the username to search for
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

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
     * Find all users with a specific role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRole(String role);

    /**
     * Find all active users.
     *
     * @return a list of active users
     */
    List<User> findByActiveTrue();

    /**
     * Find all inactive users.
     *
     * @return a list of inactive users
     */
    List<User> findByActiveFalse();

    /**
     * Find users created after a specific date.
     *
     * @param date the date to compare against
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users who haven't logged in since a specific date.
     *
     * @param date the date to compare against
     * @return a list of users who haven't logged in since the specified date
     */
    @Query("{'lastLoginAt': {$lt: ?0}}")
    List<User> findByLastLoginAtBefore(LocalDateTime date);

    /**
     * Search users by first name or last name containing the given text (case insensitive).
     *
     * @param searchText the text to search for in first or last names
     * @return a list of matching users
     */
    @Query("{'$or': [{'firstName': {$regex: ?0, $options: 'i'}}, {'lastName': {$regex: ?0, $options: 'i'}}]}")
    List<User> searchByName(String searchText);

    /**
     * Count the number of users with a specific role.
     *
     * @param role the role to count
     * @return the count of users with the specified role
     */
    long countByRole(String role);

    /**
     * Delete users who have been inactive (not logged in) for a period longer than the specified date.
     *
     * @param date the cutoff date for inactivity
     * @return the number of deleted users
     */
    @Query(value = "{'lastLoginAt': {$lt: ?0}, 'active': false}", delete = true)
    long deleteInactiveUsersBefore(LocalDateTime date);
}