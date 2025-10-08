package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if a user exists with the username, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Find users by their role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRoles(String role);

    /**
     * Find users by their organization ID.
     *
     * @param organizationId the organization ID to search for
     * @return a list of users belonging to the specified organization
     */
    List<User> findByOrganizationId(String organizationId);

    /**
     * Find users by their organization ID with pagination.
     *
     * @param organizationId the organization ID to search for
     * @param pageable pagination information
     * @return a page of users belonging to the specified organization
     */
    Page<User> findByOrganizationId(String organizationId, Pageable pageable);

    /**
     * Find users created after a specific date.
     *
     * @param date the date to compare against
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users by partial username match.
     *
     * @param username the partial username to search for
     * @return a list of users whose username contains the specified string
     */
    @Query("{ 'username': { $regex: ?0, $options: 'i' } }")
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Find users by partial email match.
     *
     * @param email the partial email to search for
     * @return a list of users whose email contains the specified string
     */
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<User> findByEmailContainingIgnoreCase(String email);

    /**
     * Find users by organization ID and role.
     *
     * @param organizationId the organization ID to search for
     * @param role the role to search for
     * @return a list of users belonging to the specified organization with the specified role
     */
    @Query("{ 'organizationId': ?0, 'roles': ?1 }")
    List<User> findByOrganizationIdAndRole(String organizationId, String role);

    /**
     * Find inactive users (users who haven't logged in recently).
     *
     * @param lastLoginDate the date threshold for considering a user inactive
     * @return a list of users who haven't logged in since the specified date
     */
    @Query("{ 'lastLoginAt': { $lt: ?0 } }")
    List<User> findInactiveUsers(LocalDateTime lastLoginDate);

    /**
     * Count users by organization ID.
     *
     * @param organizationId the organization ID to count users for
     * @return the number of users belonging to the specified organization
     */
    long countByOrganizationId(String organizationId);
}