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
     * Find all tasks by status and project
     * 
     * @param status the task status
     * @param projectId the project identifier
     * @return list of tasks with the specified status in the given project
     */
    List<Task> findByStatusAndProjectId(String status, String projectId);
    
    /**
     * Find all tasks by priority and project
     * 
     * @param priority the task priority
     * @param projectId the project identifier
     * @return list of tasks with the specified priority in the given project
     */
    List<Task> findByPriorityAndProjectId(String priority, String projectId);
    
    /**
     * Find all tasks with due date before the specified date
     * 
     * @param dueDate the reference date
     * @return list of tasks due before the specified date
     */
    List<Task> findByDueDateBefore(LocalDateTime dueDate);
    
    /**
     * Find all overdue tasks for a specific project
     * 
     * @param dueDate the reference date
     * @param projectId the project identifier
     * @return list of overdue tasks for the specified project
     */
    List<Task> findByDueDateBeforeAndProjectId(LocalDateTime dueDate, String projectId);
    
    /**
     * Find all tasks by title containing the search term (case-insensitive)
     * 
     * @param searchTerm the search term to look for in task titles
     * @return list of tasks matching the search criteria
     */
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Task> findByTitleContainingIgnoreCase(String searchTerm);
    
    /**
     * Find all tasks by description containing the search term (case-insensitive)
     * 
     * @param searchTerm the search term to look for in task descriptions
     * @return list of tasks matching the search criteria
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<Task> findByDescriptionContainingIgnoreCase(String searchTerm);
    
    /**
     * Find all tasks for a specific project and assignee
     * 
     * @param projectId the project identifier
     * @param assigneeId the assignee identifier
     * @return list of tasks for the specified project and assignee
     */
    List<Task> findByProjectIdAndAssigneeId(String projectId, String assigneeId);
    
    /**
     * Find a task by its title and project
     * 
     * @param title the task title
     * @param projectId the project identifier
     * @return optional containing the task if found
     */
    Optional<Task> findByTitleAndProjectId(String title, String projectId);
    
    /**
     * Count tasks by project
     * 
     * @param projectId the project identifier
     * @return count of tasks in the project
     */
    long countByProjectId(String projectId);
    
    /**
     * Count tasks by status and project
     * 
     * @param status the task status
     * @param projectId the project identifier
     * @return count of tasks with the specified status in the project
     */
    long countByStatusAndProjectId(String status, String projectId);
    
    /**
     * Find tasks created between two dates
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tasks created within the date range
     */
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find tasks updated between two dates
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tasks updated within the date range
     */
    List<Task> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Delete all tasks for a specific project
     * 
     * @param projectId the project identifier
     */
    void deleteByProjectId(String projectId);
}