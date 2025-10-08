package com.ecommerce.repository;

import com.ecommerce.model.User;
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
     * Find a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email address.
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
     * Find users by their status.
     *
     * @param status the user status to filter by
     * @return a list of users with the specified status
     */
    List<User> findByStatus(String status);

    /**
     * Find users by role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRolesContaining(String role);

    /**
     * Find users who haven't logged in since the specified date.
     *
     * @param date the date threshold for last login
     * @return a list of users who haven't logged in since the specified date
     */
    List<User> findByLastLoginBefore(LocalDateTime date);

    /**
     * Find users created after a specific date.
     *
     * @param date the date threshold for user creation
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Search for users by first name or last name containing the search term.
     *
     * @param searchTerm the search term to look for in names
     * @return a list of users matching the search criteria
     */
    @Query("{ $or: [ { 'firstName': { $regex: ?0, $options: 'i' } }, { 'lastName': { $regex: ?0, $options: 'i' } } ] }")
    List<User> searchByName(String searchTerm);

    /**
     * Find users with specific roles and active status.
     *
     * @param roles the roles to filter by
     * @return a list of active users with the specified roles
     */
    @Query("{ 'roles': { $in: ?0 }, 'status': 'ACTIVE' }")
    List<User> findActiveUsersByRoles(List<String> roles);

    /**
     * Count users by status.
     *
     * @param status the status to count
     * @return the number of users with the specified status
     */
    long countByStatus(String status);

    /**
     * Delete users who have been inactive since the specified date.
     *
     * @param date the date threshold for inactivity
     * @return the number of deleted users
     */
    long deleteByLastLoginBeforeAndStatusNot(LocalDateTime date, String activeStatus);
}