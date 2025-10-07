package com.example.repository;

import com.example.model.User;
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
 * Provides CRUD operations and custom query methods for User collection in MongoDB.
 * 
 * This repository supports the Enhanced User Authentication and Security Framework
 * by providing data access methods for user management operations.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by email address.
     * Used for authentication and user lookup operations.
     *
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by username.
     * Used for authentication and profile lookup operations.
     *
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists with the given email.
     * Used for validation during user registration.
     *
     * @param email the email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given username.
     * Used for validation during user registration.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Find all users with a specific role.
     * Supports role-based access control (RBAC) operations.
     *
     * @param role the role to filter by
     * @return List of users with the specified role
     */
    @Query("{ 'roles': ?0 }")
    List<User> findByRole(String role);

    /**
     * Find all users with a specific role with pagination.
     * Supports role-based access control (RBAC) operations with pagination.
     *
     * @param role the role to filter by
     * @param pageable pagination information
     * @return Page of users with the specified role
     */
    @Query("{ 'roles': ?0 }")
    Page<User> findByRole(String role, Pageable pageable);

    /**
     * Find users by active status.
     * Used for filtering active/inactive users in the system.
     *
     * @param isActive the active status to filter by
     * @return List of users with the specified active status
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Find users by active status with pagination.
     *
     * @param isActive the active status to filter by
     * @param pageable pagination information
     * @return Page of users with the specified active status
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find users created after a specific date.
     * Useful for reporting and analytics.
     *
     * @param date the date to filter from
     * @return List of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users created between two dates.
     * Useful for reporting and analytics.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return List of users created within the date range
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find users by last login date range.
     * Useful for identifying inactive users.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return List of users who logged in within the date range
     */
    @Query("{ 'lastLogin': { $gte: ?0, $lte: ?1 } }")
    List<User> findByLastLoginBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find users who haven't logged in since a specific date.
     * Useful for identifying inactive users for cleanup or re-engagement.
     *
     * @param date the date to check against
     * @return List of users who haven't logged in since the specified date
     */
    @Query("{ $or: [ { 'lastLogin': { $lt: ?0 } }, { 'lastLogin': null } ] }")
    List<User> findUsersNotLoggedInSince(LocalDateTime date);

    /**
     * Search users by username or email containing the search term.
     * Case-insensitive search for user lookup.
     *
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return Page of users matching the search criteria
     */
    @Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchByUsernameOrEmail(String searchTerm, Pageable pageable);

    /**
     * Find users by profile completion status.
     * Useful for identifying users who need to complete their profiles.
     *
     * @param isComplete the profile completion status
     * @return List of users with the specified profile completion status
     */
    @Query("{ 'profile.isComplete': ?0 }")
    List<User> findByProfileCompletion(Boolean isComplete);

    /**
     * Find users with email verification status.
     * Used for managing email verification workflows.
     *
     * @param isVerified the email verification status
     * @return List of users with the specified verification status
     */
    @Query("{ 'emailVerified': ?0 }")
    List<User> findByEmailVerificationStatus(Boolean isVerified);

    /**
     * Find users with email verification status with pagination.
     *
     * @param isVerified the email verification status
     * @param pageable pagination information
     * @return Page of users with the specified verification status
     */
    @Query("{ 'emailVerified': ?0 }")
    Page<User> findByEmailVerificationStatus(Boolean isVerified, Pageable pageable);

    /**
     * Find users with multi-factor authentication enabled.
     * Supports security auditing and compliance reporting.
     *
     * @param mfaEnabled the MFA enabled status
     * @return List of users with the specified MFA status
     */
    @Query("{ 'mfaEnabled': ?0 }")
    List<User> findByMfaStatus(Boolean mfaEnabled);

    /**
     * Count users by role.
     * Useful for analytics and reporting.
     *
     * @param role the role to count
     * @return count of users with the specified role
     */
    @Query(value = "{ 'roles': ?0 }", count = true)
    long countByRole(String role);

    /**
     * Count active users.
     * Useful for analytics and reporting.
     *
     * @return count of active users
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find users by multiple roles.
     * Supports complex role-based queries.
     *
     * @param roles list of roles to filter by
     * @return List of users having any of the specified roles
     */
    @Query("{ 'roles': { $in: ?0 } }")
    List<User> findByRolesIn(List<String> roles);

    /**
     * Find users by organization ID.
     * Used for organization-specific user management.
     *
     * @param organizationId the organization ID
     * @return List of users belonging to the organization
     */
    @Query("{ 'organizationId': ?0 }")
    List<User> findByOrganizationId(String organizationId);

    /**
     * Find users by organization ID with pagination.
     *
     * @param organizationId the organization ID
     * @param pageable pagination information
     * @return Page of users belonging to the organization
     */
    @Query("{ 'organizationId': ?0 }")
    Page<User> findByOrganizationId(String organizationId, Pageable pageable);

    /**
     * Delete users who are inactive and haven't logged in for a specified period.
     * Used for data cleanup and compliance with data retention policies.
     *
     * @param date the cutoff date for last login
     * @param isActive the active status
     * @return count of deleted users
     */
    @Query(value = "{ 'isActive': ?1, 'lastLogin': { $lt: ?0 } }", delete = true)
    long deleteInactiveUsersBefore(LocalDateTime date, Boolean isActive);

    /**
     * Find users with password reset required flag.
     * Used for security management and forced password resets.
     *
     * @param passwordResetRequired the password reset required status
     * @return List of users requiring password reset
     */
    @Query("{ 'passwordResetRequired': ?0 }")
    List<User> findByPasswordResetRequired(Boolean passwordResetRequired);

    /**
     * Update last login timestamp for a user.
     * Used to track user activity.
     *
     * @param userId the user ID
     * @param lastLogin the last login timestamp
     */
    @Query("{ '_id': ?0 }")
    void updateLastLogin(String userId, LocalDateTime lastLogin);
}