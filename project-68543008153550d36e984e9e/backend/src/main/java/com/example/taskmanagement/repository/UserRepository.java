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
     * Find a user by their email address
     * 
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by their username
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if a user exists with the given email
     * 
     * @param email the email to check
     * @return true if a user exists with this email
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * 
     * @param username the username to check
     * @return true if a user exists with this username
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users by organization ID
     * 
     * @param organizationId the organization ID to search for
     * @return list of users belonging to the organization
     */
    @Query("{'organizationIds': ?0}")
    List<User> findByOrganizationId(String organizationId);
    
    /**
     * Find users by organization ID with pagination
     * 
     * @param organizationId the organization ID to search for
     * @param pageable pagination information
     * @return page of users belonging to the organization
     */
    @Query("{'organizationIds': ?0}")
    Page<User> findByOrganizationId(String organizationId, Pageable pageable);
    
    /**
     * Find users who have been assigned to a specific project
     * 
     * @param projectId the project ID to search for
     * @return list of users assigned to the project
     */
    @Query("{'projectIds': ?0}")
    List<User> findByProjectId(String projectId);
    
    /**
     * Find users created after a specific date
     * 
     * @param date the date threshold
     * @return list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users by role
     * 
     * @param role the role to search for
     * @return list of users with the specified role
     */
    @Query("{'roles': ?0}")
    List<User> findByRole(String role);
    
    /**
     * Search users by partial username or email match
     * 
     * @param searchTerm the search term to match against username or email
     * @return list of users matching the search criteria
     */
    @Query("{'$or': [{'username': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    List<User> searchUsers(String searchTerm);
    
    /**
     * Search users by partial username or email match with pagination
     * 
     * @param searchTerm the search term to match against username or email
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("{'$or': [{'username': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Find users who have tasks assigned to them
     * 
     * @param taskId the task ID
     * @return list of users assigned to the task
     */
    @Query("{'assignedTaskIds': ?0}")
    List<User> findByAssignedTaskId(String taskId);
    
    /**
     * Find inactive users (users who haven't logged in for a specified period)
     * 
     * @param lastLoginDate the threshold date for considering a user inactive
     * @return list of inactive users
     */
    @Query("{'lastLoginAt': {$lt: ?0}}")
    List<User> findInactiveUsers(LocalDateTime lastLoginDate);
}