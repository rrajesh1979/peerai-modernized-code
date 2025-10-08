package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an organization/company entity in the system.
 * Organizations can have multiple projects and users associated with them.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "organizations")
public class Organization {

    @Id
    private String id;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Indexed(unique = true)
    private String name;

    @Field("description")
    private String description;

    @Field("address")
    private Address address;

    @Field("contactEmail")
    private String contactEmail;

    @Field("contactPhone")
    private String contactPhone;

    @Field("website")
    private String website;

    @Field("industry")
    private String industry;

    @Field("size")
    private String size;

    @Field("logoUrl")
    private String logoUrl;

    @Field("active")
    private boolean active = true;

    @Field("memberIds")
    @Builder.Default
    private List<String> memberIds = new ArrayList<>();

    @Field("adminIds")
    @Builder.Default
    private List<String> adminIds = new ArrayList<>();

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    /**
     * Represents a physical address for an organization.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }

    /**
     * Adds a user as a member of the organization.
     *
     * @param userId the ID of the user to add as a member
     * @return true if the user was added, false if already a member
     */
    public boolean addMember(String userId) {
        if (memberIds == null) {
            memberIds = new ArrayList<>();
        }
        if (!memberIds.contains(userId)) {
            return memberIds.add(userId);
        }
        return false;
    }

    /**
     * Removes a user from the organization's members.
     *
     * @param userId the ID of the user to remove
     * @return true if the user was removed, false if not a member
     */
    public boolean removeMember(String userId) {
        if (memberIds != null) {
            return memberIds.remove(userId);
        }
        return false;
    }

    /**
     * Adds a user as an admin of the organization.
     *
     * @param userId the ID of the user to add as an admin
     * @return true if the user was added, false if already an admin
     */
    public boolean addAdmin(String userId) {
        if (adminIds == null) {
            adminIds = new ArrayList<>();
        }
        
        // Ensure the user is also a member
        addMember(userId);
        
        if (!adminIds.contains(userId)) {
            return adminIds.add(userId);
        }
        return false;
    }

    /**
     * Removes a user from the organization's admins.
     *
     * @param userId the ID of the user to remove
     * @return true if the user was removed, false if not an admin
     */
    public boolean removeAdmin(String userId) {
        if (adminIds != null) {
            return adminIds.remove(userId);
        }
        return false;
    }

    /**
     * Checks if a user is a member of the organization.
     *
     * @param userId the ID of the user to check
     * @return true if the user is a member, false otherwise
     */
    public boolean isMember(String userId) {
        return memberIds != null && memberIds.contains(userId);
    }

    /**
     * Checks if a user is an admin of the organization.
     *
     * @param userId the ID of the user to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(String userId) {
        return adminIds != null && adminIds.contains(userId);
    }
}