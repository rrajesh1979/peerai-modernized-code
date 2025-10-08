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
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * 
     * @param username the username to check
     * @return true if a user exists with this username, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users by organization ID
     * 
     * @param organizationId the organization ID to search for
     * @return a list of users belonging to the organization
     */
    @Query("{'organizationIds': ?0}")
    List<User> findByOrganizationId(String organizationId);
    
    /**
     * Find users by organization ID with pagination
     * 
     * @param organizationId the organization ID to search for
     * @param pageable pagination information
     * @return a page of users belonging to the organization
     */
    @Query("{'organizationIds': ?0}")
    Page<User> findByOrganizationId(String organizationId, Pageable pageable);
    
    /**
     * Find users by role
     * 
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRoles(String role);
    
    /**
     * Find users created after a specific date
     * 
     * @param date the date to compare against
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users by partial name match (case insensitive)
     * 
     * @param namePattern the pattern to match against username
     * @return a list of users whose username contains the pattern
     */
    @Query("{'username': {$regex: ?0, $options: 'i'}}")
    List<User> findByUsernameContainingIgnoreCase(String namePattern);
    
    /**
     * Find users by partial email match (case insensitive)
     * 
     * @param emailPattern the pattern to match against email
     * @return a list of users whose email contains the pattern
     */
    @Query("{'email': {$regex: ?0, $options: 'i'}}")
    List<User> findByEmailContainingIgnoreCase(String emailPattern);
    
    /**
     * Find users assigned to a specific project
     * 
     * @param projectId the project ID to search for
     * @return a list of users assigned to the project
     */
    @Query("{'projectIds': ?0}")
    List<User> findByProjectId(String projectId);
    
    /**
     * Find users who are active
     * 
     * @return a list of active users
     */
    List<User> findByActiveTrue();
    
    /**
     * Find users who are inactive
     * 
     * @return a list of inactive users
     */
    List<User> findByActiveFalse();
    
    /**
     * Delete users by organization ID
     * 
     * @param organizationId the organization ID
     */
    @Query(value = "{'organizationIds': ?0}", delete = true)
    void deleteByOrganizationId(String organizationId);
}