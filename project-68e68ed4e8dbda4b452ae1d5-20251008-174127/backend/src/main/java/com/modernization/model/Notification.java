package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a notification entity in the system.
 * Notifications are used to inform users about various system events.
 */
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String type;
    private String title;
    private String message;
    private boolean read;

    private RelatedEntity relatedTo;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Default constructor
     */
    public Notification() {
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor for creating a notification
     *
     * @param userId    ID of the user to whom the notification is addressed
     * @param type      Type of notification (e.g., "TASK_ASSIGNED", "PROJECT_UPDATE")
     * @param title     Short title of the notification
     * @param message   Detailed message content
     * @param relatedTo Entity related to this notification
     */
    public Notification(String userId, String type, String title, String message, RelatedEntity relatedTo) {
        this();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedTo = relatedTo;
    }

    /**
     * Inner class representing an entity related to the notification
     */
    public static class RelatedEntity {
        private String type;
        private String id;

        public RelatedEntity() {
        }

        public RelatedEntity(String type, String id) {
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RelatedEntity that = (RelatedEntity) o;
            return Objects.equals(type, that.type) && Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, id);
        }

        @Override
        public String toString() {
            return "RelatedEntity{" +
                    "type='" + type + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    // Getters and Setters

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public RelatedEntity getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(RelatedEntity relatedTo) {
        this.relatedTo = relatedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Mark the notification as read
     */
    public void markAsRead() {
        this.read = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return read == that.read &&
                Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(message, that.message) &&
                Objects.equals(relatedTo, that.relatedTo) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, title, message, read, relatedTo, createdAt);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", read=" + read +
                ", relatedTo=" + relatedTo +
                ", createdAt=" + createdAt +
                '}';
    }
}