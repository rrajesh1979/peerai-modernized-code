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
     * @param organizationId the organization ID
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
    List<Project> findByMembersContaining(String userId);
    
    /**
     * Find projects created after a specific date
     * 
     * @param date the date threshold
     * @return list of projects created after the specified date
     */
    List<Project> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find projects by name containing the given text (case-insensitive)
     * 
     * @param name the text to search for in project names
     * @return list of projects with names containing the search text
     */
    List<Project> findByNameContainingIgnoreCase(String name);
    
    /**
     * Count projects by organization ID
     * 
     * @param organizationId the organization ID
     * @return count of projects for the organization
     */
    long countByOrganizationId(String organizationId);
    
    /**
     * Find projects that are due before a specific date
     * 
     * @param date the date threshold
     * @return list of projects due before the specified date
     */
    List<Project> findByDueDateBefore(LocalDateTime date);
    
    /**
     * Find active projects for a specific organization
     * 
     * @param organizationId the organization ID
     * @param status the active status value
     * @return list of active projects for the organization
     */
    List<Project> findByOrganizationIdAndStatus(String organizationId, String status);
    
    /**
     * Delete all projects for a specific organization
     * 
     * @param organizationId the organization ID
     */
    void deleteByOrganizationId(String organizationId);
    
    /**
     * Find projects by multiple statuses
     * 
     * @param statuses list of statuses to search for
     * @return list of projects with any of the specified statuses
     */
    @Query("{'status': {$in: ?0}}")
    List<Project> findByStatusIn(List<String> statuses);
}