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
     * @param organizationId the organization ID
     * @return list of projects
     */
    List<Project> findByOrganizationId(String organizationId);

    /**
     * Find all projects belonging to a specific organization with pagination
     *
     * @param organizationId the organization ID
     * @param pageable pagination information
     * @return page of projects
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
     * Check if a project with the given name exists in an organization
     *
     * @param name the project name
     * @param organizationId the organization ID
     * @return true if exists, false otherwise
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
     * Find projects by manager ID
     *
     * @param managerId the user ID of the project manager
     * @return list of projects managed by the specified user
     */
    List<Project> findByManagerId(String managerId);

    /**
     * Find projects where a user is a team member
     *
     * @param userId the user ID
     * @return list of projects where the user is a team member
     */
    @Query("{'teamMembers': ?0}")
    List<Project> findByTeamMembersContaining(String userId);

    /**
     * Find projects created after a specific date
     *
     * @param date the date threshold
     * @return list of projects created after the specified date
     */
    List<Project> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find projects with deadlines before a specific date
     *
     * @param date the date threshold
     * @return list of projects with deadlines before the specified date
     */
    List<Project> findByDeadlineBefore(LocalDateTime date);

    /**
     * Search projects by name or description containing the search term
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching projects
     */
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    Page<Project> searchByNameOrDescription(String searchTerm, Pageable pageable);

    /**
     * Find projects by organization ID and status
     *
     * @param organizationId the organization ID
     * @param status the project status
     * @return list of projects matching the criteria
     */
    List<Project> findByOrganizationIdAndStatus(String organizationId, String status);

    /**
     * Count projects by organization ID
     *
     * @param organizationId the organization ID
     * @return count of projects in the organization
     */
    long countByOrganizationId(String organizationId);

    /**
     * Delete all projects belonging to an organization
     *
     * @param organizationId the organization ID
     */
    void deleteByOrganizationId(String organizationId);
}