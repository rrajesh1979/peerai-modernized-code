package com.example.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user session for authentication and tracking user activity.
 * Sessions are stored in MongoDB and used to maintain user authentication state.
 */
@Document(collection = "sessions")
public class Session {

    @Id
    private String id;

    @Field("userId")
    @Indexed
    private String userId;

    @Field("token")
    @Indexed(unique = true)
    private String token;

    @Field("ipAddress")
    private String ipAddress;

    @Field("userAgent")
    private String userAgent;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("expiresAt")
    @Indexed
    private LocalDateTime expiresAt;

    @Field("lastActivity")
    private LocalDateTime lastActivity;

    @Field("isActive")
    @Indexed
    private Boolean isActive;

    /**
     * Default constructor required by MongoDB
     */
    public Session() {
    }

    /**
     * Constructor with required fields
     */
    public Session(String userId, String token, String ipAddress, String userAgent, 
                  LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastActivity = createdAt;
        this.isActive = true;
    }

    /**
     * Checks if the session is expired
     * 
     * @return true if the session is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Updates the last activity timestamp to the current time
     */
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Invalidates the session by setting isActive to false
     */
    public void invalidate() {
        this.isActive = false;
    }

    /**
     * Extends the session expiration time by the specified number of minutes
     * 
     * @param minutes the number of minutes to extend the session
     */
    public void extendExpiration(long minutes) {
        this.expiresAt = this.expiresAt.plusMinutes(minutes);
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) &&
               Objects.equals(token, session.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + (token != null ? "[PROTECTED]" : null) + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", lastActivity=" + lastActivity +
                ", isActive=" + isActive +
                '}';
    }
}