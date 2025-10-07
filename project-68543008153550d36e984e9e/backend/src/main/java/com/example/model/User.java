package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing core user accounts and profiles in the system.
 * This class maps to the Users collection in MongoDB and contains user authentication
 * and profile information.
 * 
 * Security considerations:
 * - Password is stored as a hash (never plain text)
 * - Email addresses are unique and indexed for fast lookup
 * - Usernames are unique and indexed
 * - Supports role-based access control through roles field
 * - Tracks account status and verification state
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Indexed(unique = true)
    @Field("email")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores and hyphens")
    @Indexed(unique = true)
    @Field("username")
    private String username;

    /**
     * Hashed password - never store plain text passwords
     * This field should be populated with BCrypt or similar secure hash
     */
    @NotBlank(message = "Password hash is required")
    @Field("passwordHash")
    private String passwordHash;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Field("firstName")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Field("lastName")
    private String lastName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s-()]*$", message = "Phone number format is invalid")
    @Field("phoneNumber")
    private String phoneNumber;

    /**
     * User roles for role-based access control (RBAC)
     * Common roles: ROLE_USER, ROLE_ADMIN, ROLE_MANAGER, ROLE_DEVELOPER
     */
    @Builder.Default
    @Field("roles")
    private Set<String> roles = new HashSet<>();

    /**
     * Account status flags
     */
    @Builder.Default
    @Field("isActive")
    private Boolean isActive = true;

    @Builder.Default
    @Field("isEmailVerified")
    private Boolean isEmailVerified = false;

    @Builder.Default
    @Field("isLocked")
    private Boolean isLocked = false;

    /**
     * Multi-factor authentication settings
     */
    @Builder.Default
    @Field("mfaEnabled")
    private Boolean mfaEnabled = false;

    @Field("mfaSecret")
    private String mfaSecret;

    /**
     * Profile and preference information
     */
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Field("bio")
    private String bio;

    @Field("avatarUrl")
    private String avatarUrl;

    @Field("timezone")
    private String timezone;

    @Field("language")
    private String language;

    /**
     * Organization membership
     * References to Organization IDs this user belongs to
     */
    @Builder.Default
    @Field("organizationIds")
    private Set<String> organizationIds = new HashSet<>();

    /**
     * Security and audit fields
     */
    @Field("lastLoginAt")
    private LocalDateTime lastLoginAt;

    @Field("lastLoginIp")
    private String lastLoginIp;

    @Builder.Default
    @Field("failedLoginAttempts")
    private Integer failedLoginAttempts = 0;

    @Field("lockedUntil")
    private LocalDateTime lockedUntil;

    @Field("passwordChangedAt")
    private LocalDateTime passwordChangedAt;

    /**
     * Password reset functionality
     */
    @Field("passwordResetToken")
    private String passwordResetToken;

    @Field("passwordResetTokenExpiry")
    private LocalDateTime passwordResetTokenExpiry;

    /**
     * Email verification
     */
    @Field("emailVerificationToken")
    private String emailVerificationToken;

    @Field("emailVerificationTokenExpiry")
    private LocalDateTime emailVerificationTokenExpiry;

    /**
     * Audit timestamps
     */
    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("createdBy")
    private String createdBy;

    @Field("updatedBy")
    private String updatedBy;

    /**
     * Soft delete support
     */
    @Field("deletedAt")
    private LocalDateTime deletedAt;

    @Field("deletedBy")
    private String deletedBy;

    /**
     * Additional metadata for extensibility
     */
    @Field("metadata")
    private java.util.Map<String, Object> metadata;

    /**
     * Helper method to check if account is locked
     * Account can be locked permanently or temporarily
     */
    public boolean isAccountLocked() {
        if (Boolean.TRUE.equals(isLocked)) {
            if (lockedUntil == null) {
                return true; // Permanently locked
            }
            return LocalDateTime.now().isBefore(lockedUntil); // Temporarily locked
        }
        return false;
    }

    /**
     * Helper method to check if user has a specific role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Helper method to add a role to the user
     */
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Helper method to remove a role from the user
     */
    public void removeRole(String role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }

    /**
     * Helper method to add organization membership
     */
    public void addOrganization(String organizationId) {
        if (this.organizationIds == null) {
            this.organizationIds = new HashSet<>();
        }
        this.organizationIds.add(organizationId);
    }

    /**
     * Helper method to remove organization membership
     */
    public void removeOrganization(String organizationId) {
        if (this.organizationIds != null) {
            this.organizationIds.remove(organizationId);
        }
    }

    /**
     * Helper method to get full name
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

    /**
     * Helper method to check if password reset token is valid
     */
    public boolean isPasswordResetTokenValid() {
        return passwordResetToken != null 
            && passwordResetTokenExpiry != null 
            && LocalDateTime.now().isBefore(passwordResetTokenExpiry);
    }

    /**
     * Helper method to check if email verification token is valid
     */
    public boolean isEmailVerificationTokenValid() {
        return emailVerificationToken != null 
            && emailVerificationTokenExpiry != null 
            && LocalDateTime.now().isBefore(emailVerificationTokenExpiry);
    }

    /**
     * Helper method to check if account is deleted (soft delete)
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Lifecycle callback to set timestamps before persistence
     */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    /**
     * Lifecycle callback to update timestamp before update
     */
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}