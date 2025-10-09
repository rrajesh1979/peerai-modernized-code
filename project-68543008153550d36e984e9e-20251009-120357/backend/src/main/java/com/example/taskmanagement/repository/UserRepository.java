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
     * Find all users belonging to a specific organization.
     *
     * @param organizationId the organization ID
     * @return a list of users in the organization
     */
    List<User> findByOrganizationId(String organizationId);

    /**
     * Find all users belonging to a specific organization with pagination.
     *
     * @param organizationId the organization ID
     * @param pageable pagination information
     * @return a page of users in the organization
     */
    Page<User> findByOrganizationId(String organizationId, Pageable pageable);

    /**
     * Find users by role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRoles(String role);

    /**
     * Find users created after a specific date.
     *
     * @param date the date threshold
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users by partial name or email match.
     *
     * @param searchTerm the search term to match against username or email
     * @param pageable pagination information
     * @return a page of matching users
     */
    @Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchByUsernameOrEmail(String searchTerm, Pageable pageable);

    /**
     * Find users assigned to a specific project.
     *
     * @param projectId the project ID
     * @return a list of users assigned to the project
     */
    @Query("{ 'projectAssignments': { $elemMatch: { 'projectId': ?0 } } }")
    List<User> findUsersByProjectId(String projectId);

    /**
     * Find inactive users (those who haven't logged in recently).
     *
     * @param lastActiveDate the date threshold for considering a user inactive
     * @return a list of inactive users
     */
    @Query("{ 'lastLoginAt': { $lt: ?0 } }")
    List<User> findInactiveUsers(LocalDateTime lastActiveDate);

    /**
     * Count users by organization.
     *
     * @param organizationId the organization ID
     * @return the count of users in the organization
     */
    long countByOrganizationId(String organizationId);
}