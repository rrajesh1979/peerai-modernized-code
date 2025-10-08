package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task within a project in the system.
 * Tasks are assigned to users and have various attributes like status, priority, and due dates.
 */
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Priority is required")
    private String priority;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate;

    private String assignedTo;

    private Double estimatedHours;
    
    private Double actualHours;

    private List<String> tags = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    @NotNull(message = "Creation date is required")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    /**
     * Represents a comment on a task
     */
    public static class Comment {
        private String id;
        private String userId;
        private String text;
        private LocalDateTime createdAt;

        public Comment() {
        }

        public Comment(String id, String userId, String text, LocalDateTime createdAt) {
            this.id = id;
            this.userId = userId;
            this.text = text;
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Comment comment = (Comment) o;
            return Objects.equals(id, comment.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    // Default constructor
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Task(String projectId, String title, String status, String priority) {
        this();
        this.projectId = projectId;
        this.title = title;
        this.status = status;
        this.priority = priority;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    /**
     * Adds a comment to the task
     * 
     * @param userId The ID of the user adding the comment
     * @param text The comment text
     * @return The newly created comment
     */
    public Comment addComment(String userId, String text) {
        Comment comment = new Comment();
        comment.setId(java.util.UUID.randomUUID().toString());
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
        return comment;
    }

    /**
     * Updates the task status and sets the updated timestamp
     * 
     * @param status The new status
     */
    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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
                ", projectId='" + projectId + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", dueDate=" + dueDate +
                ", assignedTo='" + assignedTo + '\'' +
                '}';
    }
}