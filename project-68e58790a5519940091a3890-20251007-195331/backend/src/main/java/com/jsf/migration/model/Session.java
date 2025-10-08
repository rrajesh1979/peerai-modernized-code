package com.jsf.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a user session in the system.
 * Sessions are used to track authenticated users and their active sessions.
 */
@Document(collection = "sessions")
public class Session {

    @Id
    private String sessionId;

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

    @Field("expiresAt")
    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    @Field("createdAt")
    private Instant createdAt;

    /**
     * Default constructor for Spring Data MongoDB
     */
    public Session() {
        this.createdAt = Instant.now();
    }

    /**
     * Creates a new session with the specified parameters
     *
     * @param userId     the ID of the user associated with this session
     * @param token      the authentication token
     * @param ipAddress  the IP address from which the session was created
     * @param userAgent  the user agent information
     * @param expiresAt  the expiration time for this session
     */
    public Session(String userId, String token, String ipAddress, String userAgent, Instant expiresAt) {
        this.userId = userId;
        this.token = token;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    /**
     * Checks if the session has expired
     *
     * @return true if the session has expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Extends the session expiration time by the specified number of seconds
     *
     * @param seconds the number of seconds to extend the session
     */
    public void extendExpiration(long seconds) {
        this.expiresAt = Instant.now().plusSeconds(seconds);
    }

    // Getters and Setters

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(sessionId, session.sessionId) &&
                Objects.equals(userId, session.userId) &&
                Objects.equals(token, session.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId, token);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + (token != null ? "[PROTECTED]" : null) + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                '}';
    }
}