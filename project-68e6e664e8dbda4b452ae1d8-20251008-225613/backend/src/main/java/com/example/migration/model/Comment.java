package com.example.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user comment on content items in the application.
 * Comments can be nested (replies to other comments) and include metadata
 * such as creation time, update time, and moderation status.
 */
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Indexed
    private String contentId;

    @Indexed
    private String userId;

    private String parentCommentId;
    
    private String text;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private int likes;
    
    private int flags;
    
    /**
     * Default constructor required by MongoDB
     */
    public Comment() {
    }

    /**
     * Constructor with essential fields for creating a new comment
     * 
     * @param contentId The ID of the content being commented on
     * @param userId The ID of the user creating the comment
     * @param text The comment text
     */
    public Comment(String contentId, String userId, String text) {
        this.contentId = contentId;
        this.userId = userId;
        this.text = text;
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.likes = 0;
        this.flags = 0;
    }

    /**
     * Constructor for creating a reply to another comment
     * 
     * @param contentId The ID of the content being commented on
     * @param userId The ID of the user creating the comment
     * @param parentCommentId The ID of the parent comment this is replying to
     * @param text The comment text
     */
    public Comment(String contentId, String userId, String parentCommentId, String text) {
        this(contentId, userId, text);
        this.parentCommentId = parentCommentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    /**
     * Updates the moderation status of the comment
     * Valid statuses: pending, approved, rejected, flagged
     * 
     * @param status The new status
     */
    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid comment status: " + status);
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    private boolean isValidStatus(String status) {
        return "pending".equals(status) || 
               "approved".equals(status) || 
               "rejected".equals(status) || 
               "flagged".equals(status);
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    
    /**
     * Increments the like count by one
     */
    public void incrementLikes() {
        this.likes++;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Decrements the like count by one, ensuring it doesn't go below zero
     */
    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    /**
     * Increments the flag count by one and updates status if threshold is reached
     */
    public void incrementFlags() {
        this.flags++;
        // If flags reach a threshold, automatically update status
        if (this.flags >= 5 && !"rejected".equals(this.status)) {
            this.status = "flagged";
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if this comment is a reply to another comment
     * 
     * @return true if this is a reply, false otherwise
     */
    public boolean isReply() {
        return parentCommentId != null && !parentCommentId.isEmpty();
    }
    
    /**
     * Checks if this comment is approved and visible to users
     * 
     * @return true if the comment is approved
     */
    public boolean isApproved() {
        return "approved".equals(status);
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

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", contentId='" + contentId + '\'' +
                ", userId='" + userId + '\'' +
                ", parentCommentId='" + parentCommentId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", likes=" + likes +
                ", flags=" + flags +
                '}';
    }
}