package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Entity class representing system audit logs for tracking user actions.
 * This class maps to the AuditLogs collection in MongoDB.
 */
@Document(collection = "AuditLogs")
public class AuditLog {

    @Id
    private String id;

    @Field("userId")
    @Indexed
    private String userId;

    @Field("action")
    @Indexed
    private String action;

    @Field("entityType")
    @Indexed
    private String entityType;

    @Field("entityId")
    @Indexed
    private String entityId;

    @Field("changes")
    private Map<String, Object> changes;

    @Field("ipAddress")
    private String ipAddress;

    @Field("userAgent")
    private String userAgent;

    @Field("timestamp")
    @Indexed
    private LocalDateTime timestamp;

    /**
     * Default constructor
     */
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Parameterized constructor for creating a complete audit log entry
     */
    public AuditLog(String userId, String action, String entityType, String entityId,
                   Map<String, Object> changes, String ipAddress, String userAgent) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.changes = changes;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id) &&
               Objects.equals(userId, auditLog.userId) &&
               Objects.equals(action, auditLog.action) &&
               Objects.equals(entityType, auditLog.entityType) &&
               Objects.equals(entityId, auditLog.entityId) &&
               Objects.equals(timestamp, auditLog.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, action, entityType, entityId, timestamp);
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Builder class for creating AuditLog instances with a fluent API
     */
    public static class Builder {
        private String userId;
        private String action;
        private String entityType;
        private String entityId;
        private Map<String, Object> changes;
        private String ipAddress;
        private String userAgent;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder changes(Map<String, Object> changes) {
            this.changes = changes;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(userId, action, entityType, entityId, changes, ipAddress, userAgent);
        }
    }
}