package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Entity representing system audit logs for tracking user actions.
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
    private Instant timestamp;

    /**
     * Default constructor
     */
    public AuditLog() {
        this.timestamp = Instant.now();
    }

    /**
     * Constructor with essential fields
     *
     * @param userId     ID of the user who performed the action
     * @param action     Description of the action performed
     * @param entityType Type of entity affected (e.g., "User", "Project", "Task")
     * @param entityId   ID of the affected entity
     */
    public AuditLog(String userId, String action, String entityType, String entityId) {
        this();
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Full constructor
     *
     * @param userId     ID of the user who performed the action
     * @param action     Description of the action performed
     * @param entityType Type of entity affected (e.g., "User", "Project", "Task")
     * @param entityId   ID of the affected entity
     * @param changes    Map containing the changes made to the entity
     * @param ipAddress  IP address from which the action was performed
     * @param userAgent  User agent information
     */
    public AuditLog(String userId, String action, String entityType, String entityId,
                   Map<String, Object> changes, String ipAddress, String userAgent) {
        this(userId, action, entityType, entityId);
        this.changes = changes;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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
                ", timestamp=" + timestamp +
                '}';
    }
}