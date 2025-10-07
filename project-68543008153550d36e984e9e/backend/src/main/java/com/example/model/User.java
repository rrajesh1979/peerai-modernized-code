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
 * This model supports the enhanced authentication and security framework
 * with role-based access control and comprehensive audit capabilities.
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

    @NotBlank(message = "Password hash is required")
    @Field("passwordHash")
    private String passwordHash;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("phoneNumber")
    private String phoneNumber;

    @Field("profileImageUrl")
    private String profileImageUrl;

    /**
     * User roles for role-based access control (RBAC).
     * Supports multiple roles per user for flexible permission management.
     */
    @Builder.Default
    @Field("roles")
    private Set<String> roles = new HashSet<>();

    /**
     * Indicates whether the user account is active.
     * Inactive accounts cannot authenticate or access system resources.
     */
    @Builder.Default
    @Field("isActive")
    private Boolean isActive = true;

    /**
     * Indicates whether the user's email has been verified.
     * Part of the enhanced security framework.
     */
    @Builder.Default
    @Field("isEmailVerified")
    private Boolean isEmailVerified = false;

    /**
     * Indicates whether multi-factor authentication is enabled for this user.
     * Part of the enhanced authentication security framework.
     */
    @Builder.Default
    @Field("isMfaEnabled")
    private Boolean isMfaEnabled = false;

    /**
     * Secret key for multi-factor authentication (TOTP).
     * Encrypted at rest as part of security requirements.
     */
    @Field("mfaSecret")
    private String mfaSecret;

    /**
     * Timestamp of the last successful login.
     * Used for security monitoring and audit logging.
     */
    @Field("lastLoginAt")
    private LocalDateTime lastLoginAt;

    /**
     * IP address of the last successful login.
     * Part of security monitoring framework.
     */
    @Field("lastLoginIp")
    private String lastLoginIp;

    /**
     * Counter for failed login attempts.
     * Used for account lockout security mechanism.
     */
    @Builder.Default
    @Field("failedLoginAttempts")
    private Integer failedLoginAttempts = 0;

    /**
     * Timestamp when the account was locked due to security reasons.
     * Null if account is not locked.
     */
    @Field("accountLockedUntil")
    private LocalDateTime accountLockedUntil;

    /**
     * Token for password reset functionality.
     * Temporary and expires after use or timeout.
     */
    @Field("passwordResetToken")
    private String passwordResetToken;

    /**
     * Expiration timestamp for password reset token.
     */
    @Field("passwordResetTokenExpiry")
    private LocalDateTime passwordResetTokenExpiry;

    /**
     * Token for email verification.
     */
    @Field("emailVerificationToken")
    private String emailVerificationToken;

    /**
     * Expiration timestamp for email verification token.
     */
    @Field("emailVerificationTokenExpiry")
    private LocalDateTime emailVerificationTokenExpiry;

    /**
     * Timestamp when the password was last changed.
     * Used for password expiration policies.
     */
    @Field("passwordChangedAt")
    private LocalDateTime passwordChangedAt;

    /**
     * User preferences and settings stored as key-value pairs.
     * Supports UI customization and user experience personalization.
     */
    @Field("preferences")
    private UserPreferences preferences;

    /**
     * Organization IDs that this user belongs to.
     * Supports multi-tenancy and organization-based access control.
     */
    @Builder.Default
    @Field("organizationIds")
    private Set<String> organizationIds = new HashSet<>();

    /**
     * Timestamp when the user account was created.
     * Required field for audit purposes.
     */
    @Field("createdAt")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    /**
     * ID of the user who created this account.
     * Used for audit trail and administrative tracking.
     */
    @Field("createdBy")
    private String createdBy;

    /**
     * ID of the user who last updated this account.
     */
    @Field("updatedBy")
    private String updatedBy;

    /**
     * Soft delete flag for data retention compliance.
     * Deleted users are marked but not physically removed.
     */
    @Builder.Default
    @Field("isDeleted")
    private Boolean isDeleted = false;

    /**
     * Timestamp when the user account was soft deleted.
     */
    @Field("deletedAt")
    private LocalDateTime deletedAt;

    /**
     * Additional metadata for extensibility.
     * Allows storing custom attributes without schema changes.
     */
    @Field("metadata")
    private java.util.Map<String, Object> metadata;

    /**
     * Checks if the user account is currently locked.
     * 
     * @return true if account is locked, false otherwise
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if the user has a specific role.
     * 
     * @param role the role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Adds a role to the user's role set.
     * 
     * @param role the role to add
     */
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Removes a role from the user's role set.
     * 
     * @param role the role to remove
     */
    public void removeRole(String role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }

    /**
     * Adds an organization ID to the user's organization set.
     * 
     * @param organizationId the organization ID to add
     */
    public void addOrganization(String organizationId) {
        if (this.organizationIds == null) {
            this.organizationIds = new HashSet<>();
        }
        this.organizationIds.add(organizationId);
    }

    /**
     * Removes an organization ID from the user's organization set.
     * 
     * @param organizationId the organization ID to remove
     */
    public void removeOrganization(String organizationId) {
        if (this.organizationIds != null) {
            this.organizationIds.remove(organizationId);
        }
    }

    /**
     * Gets the user's full name.
     * 
     * @return concatenated first and last name, or username if names not available
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
     * Nested class for user preferences and settings.
     * Supports UI customization and personalization features.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreferences {
        
        @Field("theme")
        private String theme;
        
        @Field("language")
        private String language;
        
        @Field("timezone")
        private String timezone;
        
        @Field("dateFormat")
        private String dateFormat;
        
        @Field("timeFormat")
        private String timeFormat;
        
        @Field("notificationsEnabled")
        private Boolean notificationsEnabled;
        
        @Field("emailNotificationsEnabled")
        private Boolean emailNotificationsEnabled;
        
        @Field("dashboardLayout")
        private String dashboardLayout;
        
        @Field("itemsPerPage")
        private Integer itemsPerPage;
        
        @Field("customSettings")
        private java.util.Map<String, Object> customSettings;
    }
}