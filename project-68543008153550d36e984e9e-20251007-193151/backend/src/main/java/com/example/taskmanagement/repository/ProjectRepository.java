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
 * Repository interface for Project entity operations
 * Provides methods to interact with the Projects collection in MongoDB
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    /**
     * Find all projects belonging to a specific organization
     * 
     * @param organizationId the organization ID
     * @return list of projects for the organization
     */
    List<Project> findByOrganizationId(String organizationId);
    
    /**
     * Find all projects belonging to a specific organization with pagination
     * 
     * @param organizationId the organization ID
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
     * Check if a project with the given name exists in the organization
     * 
     * @param name the project name
     * @param organizationId the organization ID
     * @return true if project exists, false otherwise
     */
    boolean existsByNameAndOrganizationId(String name, String organizationId);
    
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
     * @param userId the user ID
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
     * Find projects with names containing the search term (case insensitive)
     * 
     * @param searchTerm the search term
     * @return list of projects matching the search term
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Project> findByNameContainingIgnoreCase(String searchTerm);
    
    /**
     * Count projects by organization ID
     * 
     * @param organizationId the organization ID
     * @return count of projects in the organization
     */
    long countByOrganizationId(String organizationId);
    
    /**
     * Delete all projects for a specific organization
     * 
     * @param organizationId the organization ID
     */
    void deleteByOrganizationId(String organizationId);
    
    /**
     * Find projects that are active and have upcoming deadlines
     * 
     * @param status the active status
     * @param currentDate the current date
     * @param deadlineDate the deadline threshold
     * @return list of active projects with upcoming deadlines
     */
    @Query("{'status': ?0, 'createdAt': {$lte: ?1}, 'deadline': {$gte: ?1, $lte: ?2}}")
    List<Project> findActiveProjectsWithUpcomingDeadlines(String status, LocalDateTime currentDate, LocalDateTime deadlineDate);
}