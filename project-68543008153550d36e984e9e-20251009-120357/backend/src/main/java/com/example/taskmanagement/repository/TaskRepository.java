package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations.
 * Provides methods to interact with the Tasks collection in MongoDB.
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    /**
     * Find all tasks associated with a specific project
     * 
     * @param projectId the project identifier
     * @return list of tasks belonging to the project
     */
    List<Task> findByProjectId(String projectId);
    
    /**
     * Find all tasks associated with a specific project with pagination
     * 
     * @param projectId the project identifier
     * @param pageable pagination information
     * @return page of tasks belonging to the project
     */
    Page<Task> findByProjectId(String projectId, Pageable pageable);
    
    /**
     * Find all tasks assigned to a specific user
     * 
     * @param assigneeId the user identifier
     * @return list of tasks assigned to the user
     */
    List<Task> findByAssigneeId(String assigneeId);
    
    /**
     * Find all tasks assigned to a specific user with pagination
     * 
     * @param assigneeId the user identifier
     * @param pageable pagination information
     * @return page of tasks assigned to the user
     */
    Page<Task> findByAssigneeId(String assigneeId, Pageable pageable);
    
    /**
     * Find all tasks by status
     * 
     * @param status the task status
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(String status);
    
    /**
     * Find all tasks by priority
     * 
     * @param priority the task priority
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(String priority);
    
    /**
     * Find all tasks for a project with a specific status
     * 
     * @param projectId the project identifier
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<Task> findByProjectIdAndStatus(String projectId, String status);
    
    /**
     * Find all tasks for a project with a specific priority
     * 
     * @param projectId the project identifier
     * @param priority the task priority
     * @return list of tasks matching the criteria
     */
    List<Task> findByProjectIdAndPriority(String projectId, String priority);
    
    /**
     * Find all tasks assigned to a user with a specific status
     * 
     * @param assigneeId the user identifier
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<Task> findByAssigneeIdAndStatus(String assigneeId, String status);
    
    /**
     * Find tasks with due dates before a specific date
     * 
     * @param dueDate the reference date
     * @return list of tasks due before the specified date
     */
    List<Task> findByDueDateBefore(LocalDateTime dueDate);
    
    /**
     * Find overdue tasks (due date before now and not completed)
     * 
     * @param dueDate the reference date (typically current date)
     * @param status the status to exclude (typically "COMPLETED")
     * @return list of overdue tasks
     */
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime dueDate, String status);
    
    /**
     * Find tasks by title containing a specific text (case-insensitive)
     * 
     * @param titleText the text to search for in titles
     * @return list of tasks with matching titles
     */
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Task> findByTitleContainingIgnoreCase(String titleText);
    
    /**
     * Find tasks by description containing a specific text (case-insensitive)
     * 
     * @param descriptionText the text to search for in descriptions
     * @return list of tasks with matching descriptions
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<Task> findByDescriptionContainingIgnoreCase(String descriptionText);
    
    /**
     * Search tasks by title or description containing a specific text
     * 
     * @param searchText the text to search for
     * @return list of tasks matching the search criteria
     */
    @Query("{'$or': [{'title': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}]}")
    List<Task> searchByTitleOrDescription(String searchText);
    
    /**
     * Find tasks created within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tasks created within the specified range
     */
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find tasks updated within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tasks updated within the specified range
     */
    List<Task> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find a task by its title and project
     * 
     * @param title the task title
     * @param projectId the project identifier
     * @return optional containing the task if found
     */
    Optional<Task> findByTitleAndProjectId(String title, String projectId);
    
    /**
     * Count tasks by project and status
     * 
     * @param projectId the project identifier
     * @param status the task status
     * @return count of matching tasks
     */
    long countByProjectIdAndStatus(String projectId, String status);
    
    /**
     * Count tasks by assignee and status
     * 
     * @param assigneeId the user identifier
     * @param status the task status
     * @return count of matching tasks
     */
    long countByAssigneeIdAndStatus(String assigneeId, String status);
    
    /**
     * Delete all tasks associated with a project
     * 
     * @param projectId the project identifier
     */
    void deleteByProjectId(String projectId);
}