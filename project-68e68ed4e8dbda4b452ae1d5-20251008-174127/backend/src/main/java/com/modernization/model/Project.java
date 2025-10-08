package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project entity representing business projects in the system.
 * Maps to the 'projects' collection in MongoDB.
 */
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    @Indexed
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Project status is required")
    private String status;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;

    @NotNull(message = "Owner ID is required")
    private String ownerId;

    private List<TeamMember> teamMembers = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    /**
     * Represents a team member assigned to a project with their role and permissions.
     */
    public static class TeamMember {
        @NotNull(message = "User ID is required")
        private String userId;
        
        @NotNull(message = "Role is required")
        private String role;
        
        private List<String> permissions = new ArrayList<>();
        
        private LocalDateTime joinedAt;

        public TeamMember() {
        }

        public TeamMember(String userId, String role) {
            this.userId = userId;
            this.role = role;
            this.joinedAt = LocalDateTime.now();
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TeamMember that = (TeamMember) o;
            return Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }

    /**
     * Default constructor
     */
    public Project() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     */
    public Project(String name, String description, String status, LocalDate startDate, String ownerId) {
        this();
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.ownerId = ownerId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
     * Adds a team member to the project
     * 
     * @param userId The ID of the user to add
     * @param role The role of the team member
     * @return true if the member was added, false if already exists
     */
    public boolean addTeamMember(String userId, String role) {
        if (teamMembers.stream().anyMatch(member -> member.getUserId().equals(userId))) {
            return false;
        }
        
        TeamMember newMember = new TeamMember(userId, role);
        return teamMembers.add(newMember);
    }

    /**
     * Removes a team member from the project
     * 
     * @param userId The ID of the user to remove
     * @return true if the member was removed, false if not found
     */
    public boolean removeTeamMember(String userId) {
        return teamMembers.removeIf(member -> member.getUserId().equals(userId));
    }

    /**
     * Adds a tag to the project if it doesn't already exist
     * 
     * @param tag The tag to add
     * @return true if the tag was added, false if it already exists
     */
    public boolean addTag(String tag) {
        if (tags.contains(tag)) {
            return false;
        }
        return tags.add(tag);
    }

    /**
     * Updates the updatedAt timestamp to the current time
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", ownerId='" + ownerId + '\'' +
                ", teamMembers=" + teamMembers.size() +
                '}';
    }
}