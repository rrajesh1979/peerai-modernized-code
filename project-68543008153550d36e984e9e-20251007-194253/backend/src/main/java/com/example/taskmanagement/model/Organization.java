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
 * Represents an organization/company entity in the task management system.
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

    @Field("address")
    private Address address;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Builder.Default
    private List<String> memberIds = new ArrayList<>();

    @Builder.Default
    private List<String> adminIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean active = true;

    /**
     * Nested class representing an organization's address
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
     * Adds a user as a member to the organization
     * 
     * @param userId the ID of the user to add as a member
     * @return true if the user was added, false if already a member
     */
    public boolean addMember(String userId) {
        if (memberIds.contains(userId)) {
            return false;
        }
        return memberIds.add(userId);
    }

    /**
     * Removes a user from the organization's members
     * 
     * @param userId the ID of the user to remove
     * @return true if the user was removed, false if not a member
     */
    public boolean removeMember(String userId) {
        return memberIds.remove(userId);
    }

    /**
     * Adds a user as an admin to the organization
     * 
     * @param userId the ID of the user to add as an admin
     * @return true if the user was added, false if already an admin
     */
    public boolean addAdmin(String userId) {
        if (adminIds.contains(userId)) {
            return false;
        }
        
        // Ensure the user is also a member
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
        
        return adminIds.add(userId);
    }

    /**
     * Removes a user from the organization's admins
     * 
     * @param userId the ID of the user to remove
     * @return true if the user was removed, false if not an admin
     */
    public boolean removeAdmin(String userId) {
        return adminIds.remove(userId);
    }

    /**
     * Checks if a user is a member of the organization
     * 
     * @param userId the ID of the user to check
     * @return true if the user is a member, false otherwise
     */
    public boolean isMember(String userId) {
        return memberIds.contains(userId);
    }

    /**
     * Checks if a user is an admin of the organization
     * 
     * @param userId the ID of the user to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(String userId) {
        return adminIds.contains(userId);
    }
}