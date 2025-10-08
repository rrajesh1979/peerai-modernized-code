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
     * Find all tasks with due date before a specific date
     * 
     * @param dueDate the reference date
     * @return list of tasks due before the specified date
     */
    List<Task> findByDueDateBefore(LocalDateTime dueDate);
    
    /**
     * Find all overdue tasks (due date before now and not completed)
     * 
     * @param now the current date/time
     * @param completedStatus the status representing completed tasks
     * @return list of overdue tasks
     */
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime now, String completedStatus);
    
    /**
     * Find a task by title and project
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
     * @return count of tasks matching criteria
     */
    long countByProjectIdAndStatus(String projectId, String status);
    
    /**
     * Find tasks by tag
     * 
     * @param tag the tag to search for
     * @return list of tasks with the specified tag
     */
    @Query("{ 'tags': ?0 }")
    List<Task> findByTag(String tag);
    
    /**
     * Find tasks by multiple tags (tasks containing any of the specified tags)
     * 
     * @param tags list of tags to search for
     * @return list of tasks containing any of the specified tags
     */
    @Query("{ 'tags': { $in: ?0 } }")
    List<Task> findByTagsIn(List<String> tags);
    
    /**
     * Find tasks by text search in title and description
     * 
     * @param searchText the text to search for
     * @return list of tasks matching the search text
     */
    @Query("{ $text: { $search: ?0 } }")
    List<Task> searchByText(String searchText);
    
    /**
     * Find tasks created by a specific user
     * 
     * @param creatorId the user identifier
     * @return list of tasks created by the user
     */
    List<Task> findByCreatorId(String creatorId);
    
    /**
     * Find tasks updated after a specific date
     * 
     * @param date the reference date
     * @return list of tasks updated after the specified date
     */
    List<Task> findByUpdatedAtAfter(LocalDateTime date);
    
    /**
     * Delete all tasks associated with a project
     * 
     * @param projectId the project identifier
     */
    void deleteByProjectId(String projectId);
}