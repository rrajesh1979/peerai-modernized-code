package com.jsf.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * User entity representing a user account in the system.
 * Maps to the 'users' collection in MongoDB.
 */
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password hash is required")
    private String passwordHash;

    private Profile profile;
    
    private Map<String, Object> preferences = new HashMap<>();
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private boolean active = true;

    /**
     * Default constructor for User
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     * 
     * @param username the username
     * @param email the email address
     * @param passwordHash the hashed password
     */
    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profile = new Profile();
    }

    /**
     * Nested class representing user profile information
     */
    public static class Profile {
        private String firstName;
        private String lastName;
        private String role;
        private LocalDateTime lastLogin;

        public Profile() {
        }

        public Profile(String firstName, String lastName, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public LocalDateTime getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
        }
        
        public String getFullName() {
            return (firstName != null ? firstName : "") + " " + 
                   (lastName != null ? lastName : "").trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Profile profile = (Profile) o;
            return Objects.equals(firstName, profile.firstName) &&
                   Objects.equals(lastName, profile.lastName) &&
                   Objects.equals(role, profile.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName, role);
        }
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }
    
    public void addPreference(String key, Object value) {
        if (this.preferences == null) {
            this.preferences = new HashMap<>();
        }
        this.preferences.put(key, value);
    }
    
    public Object getPreference(String key) {
        return this.preferences != null ? this.preferences.get(key) : null;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Updates the lastLogin timestamp in the user profile
     */
    public void updateLastLogin() {
        if (this.profile == null) {
            this.profile = new Profile();
        }
        this.profile.setLastLogin(LocalDateTime.now());
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) ||
               (Objects.equals(username, user.username) && 
                Objects.equals(email, user.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profile=" + (profile != null ? 
                    "{firstName='" + profile.firstName + "', lastName='" + profile.lastName + 
                    "', role='" + profile.role + "'}" : "null") +
                ", active=" + active +
                '}';
    }
}