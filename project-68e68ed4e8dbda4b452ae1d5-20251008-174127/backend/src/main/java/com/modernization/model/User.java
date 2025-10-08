package com.modernization.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User entity representing user account information stored in MongoDB.
 * Contains core user data and authentication details.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

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

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    private List<String> roles = new ArrayList<>();

    private boolean active = true;

    private LocalDateTime lastLogin;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Default constructor for User
     */
    public User() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param username     unique username for the user
     * @param email        user's email address
     * @param passwordHash hashed password for security
     */
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /**
     * Full constructor for User
     * 
     * @param username     unique username for the user
     * @param email        user's email address
     * @param passwordHash hashed password for security
     * @param firstName    user's first name
     * @param lastName     user's last name
     * @param roles        list of roles assigned to the user
     * @param active       whether the user account is active
     */
    public User(String username, String email, String passwordHash, String firstName, String lastName,
                List<String> roles, boolean active) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.active = active;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
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

    /**
     * Add a role to the user
     * 
     * @param role the role to add
     * @return true if the role was added, false if it already existed
     */
    public boolean addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role)) {
            return roles.add(role);
        }
        return false;
    }

    /**
     * Remove a role from the user
     * 
     * @param role the role to remove
     * @return true if the role was removed, false if it didn't exist
     */
    public boolean removeRole(String role) {
        if (roles != null) {
            return roles.remove(role);
        }
        return false;
    }

    /**
     * Check if the user has a specific role
     * 
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Get the full name of the user
     * 
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                '}';
    }
}