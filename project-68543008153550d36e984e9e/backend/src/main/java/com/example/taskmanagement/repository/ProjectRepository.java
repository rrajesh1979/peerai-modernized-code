package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity operations.
 * Provides methods to interact with the Projects collection in MongoDB.
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    /**
     * Find all projects belonging to a specific organization
     * 
     * @param organizationId the ID of the organization
     * @return list of projects for the organization
     */
    List<Project> findByOrganizationId(String organizationId);
    
    /**
     * Find all projects belonging to a specific organization with pagination
     * 
     * @param organizationId the ID of the organization
     * @param pageable pagination information
     * @return page of projects for the organization
     */
    Page<Project> findByOrganizationId(String organizationId, Pageable pageable);
    
    /**
     * Find a project by its name and organization ID
     * 
     * @param name the project name
     * @param organizationId the ID of the organization
     * @return optional containing the project if found
     */
    Optional<Project> findByNameAndOrganizationId(String name, String organizationId);
    
    /**
     * Find projects by status
     * 
     * @param status the project status
     * @return list of projects with the specified status
     */
    List<Project> findByStatus(String status);
    
    /**
     * Find projects where a specific user is a member
     * 
     * @param userId the ID of the user
     * @return list of projects where the user is a member
     */
    @Query("{'members': ?0}")
    List<Project> findProjectsByMemberId(String userId);
    
    /**
     * Find projects created after a specific date
     * 
     * @param date the date threshold
     * @return list of projects created after the specified date
     */
    List<Project> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find projects by name containing the search term (case insensitive)
     * 
     * @param searchTerm the search term to look for in project names
     * @return list of projects matching the search term
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Project> findByNameContainingIgnoreCase(String searchTerm);
    
    /**
     * Count projects by organization ID
     * 
     * @param organizationId the ID of the organization
     * @return count of projects for the organization
     */
    long countByOrganizationId(String organizationId);
    
    /**
     * Find projects with deadlines approaching within the specified days
     * 
     * @param deadline the deadline threshold
     * @return list of projects with approaching deadlines
     */
    List<Project> findByDeadlineBefore(LocalDateTime deadline);
    
    /**
     * Delete all projects for a specific organization
     * 
     * @param organizationId the ID of the organization
     */
    void deleteByOrganizationId(String organizationId);
    
    /**
     * Find projects by organization ID and status
     * 
     * @param organizationId the ID of the organization
     * @param status the project status
     * @return list of projects matching the criteria
     */
    List<Project> findByOrganizationIdAndStatus(String organizationId, String status);
    
    /**
     * Check if a project with the given name exists in the organization
     * 
     * @param name the project name
     * @param organizationId the ID of the organization
     * @return true if a project exists with the name in the organization
     */
    boolean existsByNameAndOrganizationId(String name, String organizationId);
}