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
     * @param ownerId the ID of the project owner
     * @return list of projects owned by the user
     */
    List<Project> findByOwnerId(String ownerId);

    /**
     * Find all projects owned by a specific user with pagination.
     *
     * @param ownerId the ID of the project owner
     * @param pageable pagination information
     * @return page of projects owned by the user
     */
    Page<Project> findByOwnerId(String ownerId, Pageable pageable);

    /**
     * Find all projects where a user is a team member.
     *
     * @param userId the ID of the team member
     * @return list of projects where the user is a team member
     */
    @Query("{'teamMembers.userId': ?0}")
    List<Project> findByTeamMembersUserId(String userId);

    /**
     * Find all projects where a user is a team member with pagination.
     *
     * @param userId the ID of the team member
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
     * Find projects with end dates before a specific date.
     *
     * @param date the reference date
     * @return list of projects ending before the specified date
     */
    List<Project> findByEndDateBefore(LocalDate date);

    /**
     * Find projects with start dates after a specific date.
     *
     * @param date the reference date
     * @return list of projects starting after the specified date
     */
    List<Project> findByStartDateAfter(LocalDate date);

    /**
     * Find projects within a date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of projects within the specified date range
     */
    List<Project> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);

    /**
     * Find projects by budget greater than or equal to the specified amount.
     *
     * @param budget the minimum budget amount
     * @return list of projects with budgets greater than or equal to the specified amount
     */
    List<Project> findByBudgetGreaterThanEqual(Double budget);

    /**
     * Find projects containing the specified text in name or description.
     *
     * @param text the search text
     * @return list of projects matching the search criteria
     */
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    List<Project> searchByNameOrDescription(String text);

    /**
     * Find projects containing the specified text in name or description with pagination.
     *
     * @param text the search text
     * @param pageable pagination information
     * @return page of projects matching the search criteria
     */
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    Page<Project> searchByNameOrDescription(String text, Pageable pageable);

    /**
     * Count projects by status.
     *
     * @param status the project status
     * @return count of projects with the specified status
     */
    long countByStatus(String status);

    /**
     * Count projects by owner.
     *
     * @param ownerId the ID of the project owner
     * @return count of projects owned by the specified user
     */
    long countByOwnerId(String ownerId);

    /**
     * Delete projects by owner.
     *
     * @param ownerId the ID of the project owner
     */
    void deleteByOwnerId(String ownerId);

    /**
     * Check if a project exists with the given name.
     *
     * @param name the project name
     * @return true if a project with the name exists, false otherwise
     */
    boolean existsByName(String name);
}