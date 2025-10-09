package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task entity in the task management system.
 * Tasks are associated with projects and can be assigned to users.
 */
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotNull(message = "Project ID is required")
    @Indexed
    private String projectId;

    private String assigneeId;

    private TaskStatus status = TaskStatus.OPEN;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDateTime dueDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private List<String> tags = new ArrayList<>();

    private List<String> attachmentIds = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    /**
     * Represents the possible statuses of a task.
     */
    public enum TaskStatus {
        OPEN, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED
    }

    /**
     * Represents the priority levels for a task.
     */
    public enum TaskPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Nested class to represent comments on a task.
     */
    public static class Comment {
        private String id;
        private String userId;
        private String content;
        private LocalDateTime createdAt;

        public Comment() {
        }

        public Comment(String userId, String content) {
            this.userId = userId;
            this.content = content;
            this.createdAt = LocalDateTime.now();
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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
    }

    // Constructor with required fields
    public Task(String title, String projectId) {
        this.title = title;
        this.projectId = projectId;
        this.createdAt = LocalDateTime.now();
    }

    // Full constructor
    public Task(String title, String description, String projectId, String assigneeId,
                TaskStatus status, TaskPriority priority, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<String> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Adds a comment to the task.
     *
     * @param userId  The ID of the user adding the comment
     * @param content The content of the comment
     * @return The newly created comment
     */
    public Comment addComment(String userId, String content) {
        Comment comment = new Comment(userId, content);
        this.comments.add(comment);
        return comment;
    }

    /**
     * Adds a tag to the task if it doesn't already exist.
     *
     * @param tag The tag to add
     * @return true if the tag was added, false if it already existed
     */
    public boolean addTag(String tag) {
        if (!this.tags.contains(tag)) {
            return this.tags.add(tag);
        }
        return false;
    }

    /**
     * Adds an attachment ID to the task.
     *
     * @param attachmentId The ID of the attachment to add
     */
    public void addAttachment(String attachmentId) {
        this.attachmentIds.add(attachmentId);
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
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}