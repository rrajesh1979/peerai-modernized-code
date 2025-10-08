package com.modernization.repository;

import com.modernization.model.User;
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
     * Find users by their role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRolesContaining(String role);

    /**
     * Find active users.
     *
     * @return a list of active users
     */
    List<User> findByActiveTrue();

    /**
     * Find inactive users.
     *
     * @return a list of inactive users
     */
    List<User> findByActiveFalse();

    /**
     * Find users who haven't logged in since the specified date.
     *
     * @param date the cutoff date
     * @return a list of users who haven't logged in since the specified date
     */
    List<User> findByLastLoginBefore(LocalDateTime date);

    /**
     * Find users created after the specified date.
     *
     * @param date the cutoff date
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Search for users by first name or last name containing the search term.
     *
     * @param searchTerm the search term
     * @return a list of users matching the search criteria
     */
    @Query("{ $or: [ { 'firstName': { $regex: ?0, $options: 'i' } }, { 'lastName': { $regex: ?0, $options: 'i' } } ] }")
    List<User> searchByName(String searchTerm);

    /**
     * Find users by their first and last name.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @return a list of users with the specified first and last name
     */
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Count users by role.
     *
     * @param role the role to count
     * @return the number of users with the specified role
     */
    long countByRolesContaining(String role);

    /**
     * Delete users who have been inactive since the specified date.
     *
     * @param date the cutoff date
     * @return the number of deleted users
     */
    long deleteByActiveIsFalseAndLastLoginBefore(LocalDateTime date);
}