package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an organization/company entity in the system.
 * Organizations can have multiple projects and users associated with them.
 */
@Document(collection = "organizations")
public class Organization {

    @Id
    private String id;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Indexed(unique = true)
    private String name;

    private Address address;
    
    private String description;
    
    private String website;
    
    private String logoUrl;
    
    private List<String> tags = new ArrayList<>();
    
    private List<String> adminUserIds = new ArrayList<>();
    
    private List<String> memberUserIds = new ArrayList<>();
    
    private OrganizationSettings settings = new OrganizationSettings();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private boolean active = true;

    /**
     * Default constructor for Organization
     */
    public Organization() {
    }

    /**
     * Constructor with required fields
     * 
     * @param name The name of the organization
     */
    public Organization(String name) {
        this.name = name;
    }

    /**
     * Full constructor for Organization
     * 
     * @param name The name of the organization
     * @param address The address of the organization
     * @param description A description of the organization
     * @param website The organization's website URL
     */
    public Organization(String name, Address address, String description, String website) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.website = website;
    }

    // Getters and Setters
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getAdminUserIds() {
        return adminUserIds;
    }

    public void setAdminUserIds(List<String> adminUserIds) {
        this.adminUserIds = adminUserIds;
    }

    public List<String> getMemberUserIds() {
        return memberUserIds;
    }

    public void setMemberUserIds(List<String> memberUserIds) {
        this.memberUserIds = memberUserIds;
    }

    public OrganizationSettings getSettings() {
        return settings;
    }

    public void setSettings(OrganizationSettings settings) {
        this.settings = settings;
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
     * Add a user as an admin to the organization
     * 
     * @param userId The ID of the user to add as admin
     * @return true if the user was added, false if already present
     */
    public boolean addAdmin(String userId) {
        if (!adminUserIds.contains(userId)) {
            return adminUserIds.add(userId);
        }
        return false;
    }

    /**
     * Add a user as a member to the organization
     * 
     * @param userId The ID of the user to add as member
     * @return true if the user was added, false if already present
     */
    public boolean addMember(String userId) {
        if (!memberUserIds.contains(userId)) {
            return memberUserIds.add(userId);
        }
        return false;
    }

    /**
     * Remove a user from the organization (both admin and member roles)
     * 
     * @param userId The ID of the user to remove
     * @return true if the user was removed from any role
     */
    public boolean removeUser(String userId) {
        boolean removedFromAdmin = adminUserIds.remove(userId);
        boolean removedFromMember = memberUserIds.remove(userId);
        return removedFromAdmin || removedFromMember;
    }

    /**
     * Check if a user is an admin of this organization
     * 
     * @param userId The ID of the user to check
     * @return true if the user is an admin
     */
    public boolean isAdmin(String userId) {
        return adminUserIds.contains(userId);
    }

    /**
     * Check if a user is a member of this organization
     * 
     * @param userId The ID of the user to check
     * @return true if the user is a member
     */
    public boolean isMember(String userId) {
        return memberUserIds.contains(userId);
    }

    /**
     * Check if a user is associated with this organization in any role
     * 
     * @param userId The ID of the user to check
     * @return true if the user is an admin or member
     */
    public boolean hasUser(String userId) {
        return isAdmin(userId) || isMember(userId);
    }

    /**
     * Add a tag to the organization
     * 
     * @param tag The tag to add
     * @return true if the tag was added, false if already present
     */
    public boolean addTag(String tag) {
        if (!tags.contains(tag)) {
            return tags.add(tag);
        }
        return false;
    }

    /**
     * Remove a tag from the organization
     * 
     * @param tag The tag to remove
     * @return true if the tag was removed
     */
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", adminCount=" + (adminUserIds != null ? adminUserIds.size() : 0) +
                ", memberCount=" + (memberUserIds != null ? memberUserIds.size() : 0) +
                '}';
    }

    /**
     * Nested class representing an organization's address
     */
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        public Address() {
        }

        public Address(String street, String city, String state, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    /**
     * Nested class representing organization settings
     */
    public static class OrganizationSettings {
        private boolean allowPublicProjects = false;
        private boolean requireMemberApproval = true;
        private String defaultProjectRole = "MEMBER";
        private int maxProjectsAllowed = 50;

        public OrganizationSettings() {
        }

        public boolean isAllowPublicProjects() {
            return allowPublicProjects;
        }

        public void setAllowPublicProjects(boolean allowPublicProjects) {
            this.allowPublicProjects = allowPublicProjects;
        }

        public boolean isRequireMemberApproval() {
            return requireMemberApproval;
        }

        public void setRequireMemberApproval(boolean requireMemberApproval) {
            this.requireMemberApproval = requireMemberApproval;
        }

        public String getDefaultProjectRole() {
            return defaultProjectRole;
        }

        public void setDefaultProjectRole(String defaultProjectRole) {
            this.defaultProjectRole = defaultProjectRole;
        }

        public int getMaxProjectsAllowed() {
            return maxProjectsAllowed;
        }

        public void setMaxProjectsAllowed(int maxProjectsAllowed) {
            this.maxProjectsAllowed = maxProjectsAllowed;
        }
    }
}