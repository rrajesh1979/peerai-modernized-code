package com.modernization.repository;

import com.modernization.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity operations.
 * Provides methods to interact with the Projects collection in MongoDB.
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    /**
     * Find a project by its name.
     *
     * @param name the project name
     * @return an Optional containing the project if found
     */
    Optional<Project> findByName(String name);

    /**
     * Find all projects owned by a specific user.
     *
     * @param ownerId the user ID of the project owner
     * @return list of projects owned by the user
     */
    List<Project> findByOwnerId(String ownerId);

    /**
     * Find all projects owned by a specific user with pagination.
     *
     * @param ownerId the user ID of the project owner
     * @param pageable pagination information
     * @return page of projects owned by the user
     */
    Page<Project> findByOwnerId(String ownerId, Pageable pageable);

    /**
     * Find all projects where a user is a team member.
     *
     * @param userId the user ID to search for in team members
     * @return list of projects where the user is a team member
     */
    @Query("{'teamMembers.userId': ?0}")
    List<Project> findByTeamMembersUserId(String userId);

    /**
     * Find all projects where a user is a team member with pagination.
     *
     * @param userId the user ID to search for in team members
     * @param pageable pagination information
     * @return page of projects where the user is a team member
     */
    @Query("{'teamMembers.userId': ?0}")
    Page<Project> findByTeamMembersUserId(String userId, Pageable pageable);

    /**
     * Find all projects by status.
     *
     * @param status the project status
     * @return list of projects with the specified status
     */
    List<Project> findByStatus(String status);

    /**
     * Find all projects by status with pagination.
     *
     * @param status the project status
     * @param pageable pagination information
     * @return page of projects with the specified status
     */
    Page<Project> findByStatus(String status, Pageable pageable);

    /**
     * Find projects with start date after the specified date.
     *
     * @param date the reference date
     * @return list of projects starting after the specified date
     */
    List<Project> findByStartDateAfter(LocalDate date);

    /**
     * Find projects with end date before the specified date.
     *
     * @param date the reference date
     * @return list of projects ending before the specified date
     */
    List<Project> findByEndDateBefore(LocalDate date);

    /**
     * Find active projects (end date is after current date).
     *
     * @param currentDate the current date
     * @return list of active projects
     */
    List<Project> findByEndDateAfter(LocalDate currentDate);

    /**
     * Find projects containing the specified text in name or description.
     *
     * @param searchText the text to search for
     * @param pageable pagination information
     * @return page of projects matching the search criteria
     */
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    Page<Project> searchProjects(String searchText, Pageable pageable);

    /**
     * Find projects by multiple statuses.
     *
     * @param statuses list of statuses to include
     * @param pageable pagination information
     * @return page of projects with any of the specified statuses
     */
    Page<Project> findByStatusIn(List<String> statuses, Pageable pageable);

    /**
     * Count projects by owner.
     *
     * @param ownerId the owner's user ID
     * @return count of projects owned by the specified user
     */
    long countByOwnerId(String ownerId);

    /**
     * Count projects by status.
     *
     * @param status the project status
     * @return count of projects with the specified status
     */
    long countByStatus(String status);

    /**
     * Find projects that are within the date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of projects within the date range
     */
    @Query("{'startDate': {$gte: ?0}, 'endDate': {$lte: ?1}}")
    List<Project> findProjectsInDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find projects with budget greater than the specified amount.
     *
     * @param budget the minimum budget amount
     * @return list of projects with budget greater than the specified amount
     */
    List<Project> findByBudgetGreaterThan(double budget);
}