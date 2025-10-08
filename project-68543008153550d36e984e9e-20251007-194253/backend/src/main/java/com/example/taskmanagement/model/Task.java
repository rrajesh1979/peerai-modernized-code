package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task within a project in the task management system.
 * Tasks are the core work items that can be assigned to users and tracked through various statuses.
 */
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Project ID is required")
    @Indexed
    private String projectId;

    private String assigneeId;

    private TaskStatus status = TaskStatus.TODO;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDateTime dueDate;

    private List<String> labels = new ArrayList<>();

    private List<String> attachmentIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;

    private Integer estimatedHours;

    private Integer loggedHours;

    /**
     * Enum representing the possible statuses of a task.
     */
    public enum TaskStatus {
        TODO, IN_PROGRESS, REVIEW, DONE, ARCHIVED
    }

    /**
     * Enum representing the possible priorities of a task.
     */
    public enum TaskPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Default constructor
    public Task() {
    }

    /**
     * Constructor with required fields.
     *
     * @param title     The title of the task
     * @param projectId The ID of the project this task belongs to
     */
    public Task(String title, String projectId) {
        this.title = title;
        this.projectId = projectId;
    }

    /**
     * Full constructor for all fields.
     */
    public Task(String id, String title, String description, String projectId, String assigneeId,
                TaskStatus status, TaskPriority priority, LocalDateTime dueDate, List<String> labels,
                List<String> attachmentIds, LocalDateTime createdAt, LocalDateTime updatedAt,
                String createdBy, Integer estimatedHours, Integer loggedHours) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.labels = labels;
        this.attachmentIds = attachmentIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.estimatedHours = estimatedHours;
        this.loggedHours = loggedHours;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<String> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getLoggedHours() {
        return loggedHours;
    }

    public void setLoggedHours(Integer loggedHours) {
        this.loggedHours = loggedHours;
    }

    /**
     * Add a label to the task.
     *
     * @param label The label to add
     * @return true if the label was added, false if it already existed
     */
    public boolean addLabel(String label) {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        if (!labels.contains(label)) {
            return labels.add(label);
        }
        return false;
    }

    /**
     * Remove a label from the task.
     *
     * @param label The label to remove
     * @return true if the label was removed, false if it didn't exist
     */
    public boolean removeLabel(String label) {
        if (labels != null) {
            return labels.remove(label);
        }
        return false;
    }

    /**
     * Add an attachment ID to the task.
     *
     * @param attachmentId The attachment ID to add
     * @return true if the attachment ID was added, false if it already existed
     */
    public boolean addAttachment(String attachmentId) {
        if (attachmentIds == null) {
            attachmentIds = new ArrayList<>();
        }
        if (!attachmentIds.contains(attachmentId)) {
            return attachmentIds.add(attachmentId);
        }
        return false;
    }

    /**
     * Remove an attachment ID from the task.
     *
     * @param attachmentId The attachment ID to remove
     * @return true if the attachment ID was removed, false if it didn't exist
     */
    public boolean removeAttachment(String attachmentId) {
        if (attachmentIds != null) {
            return attachmentIds.remove(attachmentId);
        }
        return false;
    }

    /**
     * Check if the task is overdue.
     *
     * @return true if the task has a due date and it's in the past
     */
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && status != TaskStatus.DONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", projectId='" + projectId + '\'' +
                ", assigneeId='" + assigneeId + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}