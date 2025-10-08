package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task within a project in the task management system.
 * Tasks are the core work items that can be assigned to users and tracked through various states.
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

    private TaskStatus status = TaskStatus.TODO;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private String assigneeId;

    private LocalDateTime dueDate;

    private List<String> labels = new ArrayList<>();

    private List<String> attachmentIds = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;

    /**
     * Represents the possible statuses of a task.
     */
    public enum TaskStatus {
        TODO, IN_PROGRESS, REVIEW, DONE, ARCHIVED
    }

    /**
     * Represents the priority levels for a task.
     */
    public enum TaskPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Represents a comment on a task.
     */
    public static class Comment {
        private String id;
        private String content;
        private String authorId;
        private LocalDateTime createdAt;

        public Comment() {
        }

        public Comment(String content, String authorId) {
            this.content = content;
            this.authorId = authorId;
            this.createdAt = LocalDateTime.now();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthorId() {
            return authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
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
        this.updatedAt = LocalDateTime.now();
    }

    // Full constructor
    public Task(String title, String description, String projectId, TaskStatus status,
                TaskPriority priority, String assigneeId, LocalDateTime dueDate,
                List<String> labels, String createdBy) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.status = status;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.labels = labels != null ? labels : new ArrayList<>();
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
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
     * Adds a label to the task if it doesn't already exist.
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
     * Removes a label from the task.
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
     * Adds a comment to the task.
     *
     * @param content  The comment content
     * @param authorId The ID of the user adding the comment
     * @return The newly created comment
     */
    public Comment addComment(String content, String authorId) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        Comment comment = new Comment(content, authorId);
        comments.add(comment);
        return comment;
    }

    /**
     * Adds an attachment ID to the task.
     *
     * @param attachmentId The ID of the attachment to add
     * @return true if the attachment was added, false if it already existed
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
     * Removes an attachment ID from the task.
     *
     * @param attachmentId The ID of the attachment to remove
     * @return true if the attachment was removed, false if it didn't exist
     */
    public boolean removeAttachment(String attachmentId) {
        if (attachmentIds != null) {
            return attachmentIds.remove(attachmentId);
        }
        return false;
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
                ", assigneeId='" + assigneeId + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}