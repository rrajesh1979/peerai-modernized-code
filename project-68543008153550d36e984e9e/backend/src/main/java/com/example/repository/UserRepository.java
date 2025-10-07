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
 * by providing efficient data access methods for user management operations.
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
    List<User> findByRole(String role);

    /**
     * Find all users with a specific role with pagination.
     * Supports efficient retrieval of large user sets.
     *
     * @param role the role to filter by
     * @param pageable pagination information
     * @return Page of users with the specified role
     */
    Page<User> findByRole(String role, Pageable pageable);

    /**
     * Find all active users.
     * Used for filtering out inactive or disabled accounts.
     *
     * @param isActive the active status to filter by
     * @return List of users matching the active status
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Find all active users with pagination.
     *
     * @param isActive the active status to filter by
     * @param pageable pagination information
     * @return Page of users matching the active status
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find users by email verification status.
     * Used for managing email verification workflows.
     *
     * @param isEmailVerified the email verification status
     * @return List of users with the specified verification status
     */
    List<User> findByIsEmailVerified(Boolean isEmailVerified);

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
    List<User> findByLastLoginBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find users who haven't logged in since a specific date.
     * Useful for identifying dormant accounts.
     *
     * @param date the date to compare against
     * @return List of users who haven't logged in since the date
     */
    List<User> findByLastLoginBefore(LocalDateTime date);

    /**
     * Search users by username or email containing the search term.
     * Case-insensitive search for user lookup.
     *
     * @param username the username search term
     * @param email the email search term
     * @param pageable pagination information
     * @return Page of users matching the search criteria
     */
    @Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }")
    Page<User> searchByUsernameOrEmail(String username, String email, Pageable pageable);

    /**
     * Find users with specific roles and active status.
     * Supports complex filtering for user management.
     *
     * @param roles list of roles to filter by
     * @param isActive the active status
     * @return List of users matching the criteria
     */
    @Query("{ 'role': { $in: ?0 }, 'isActive': ?1 }")
    List<User> findByRolesAndActiveStatus(List<String> roles, Boolean isActive);

    /**
     * Find users by profile completion status.
     * Useful for onboarding workflows.
     *
     * @param isProfileComplete the profile completion status
     * @param pageable pagination information
     * @return Page of users with the specified profile completion status
     */
    Page<User> findByIsProfileComplete(Boolean isProfileComplete, Pageable pageable);

    /**
     * Count users by role.
     * Used for analytics and reporting.
     *
     * @param role the role to count
     * @return the number of users with the specified role
     */
    long countByRole(String role);

    /**
     * Count active users.
     * Used for analytics and reporting.
     *
     * @param isActive the active status
     * @return the number of users with the specified active status
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find users by organization ID.
     * Supports multi-tenancy and organization-based filtering.
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
     * Find users requiring password reset.
     * Identifies users with expired passwords or forced reset flags.
     *
     * @param requiresPasswordReset the password reset requirement flag
     * @return List of users requiring password reset
     */
    @Query("{ 'requiresPasswordReset': ?0 }")
    List<User> findByRequiresPasswordReset(Boolean requiresPasswordReset);

    /**
     * Find users with MFA enabled.
     * Supports security auditing and compliance reporting.
     *
     * @param mfaEnabled the MFA enabled status
     * @return List of users with the specified MFA status
     */
    @Query("{ 'mfaEnabled': ?0 }")
    List<User> findByMfaEnabled(Boolean mfaEnabled);

    /**
     * Delete users who are inactive and created before a specific date.
     * Used for data retention and cleanup operations.
     *
     * @param isActive the active status
     * @param date the date threshold
     * @return the number of deleted users
     */
    long deleteByIsActiveAndCreatedAtBefore(Boolean isActive, LocalDateTime date);
}