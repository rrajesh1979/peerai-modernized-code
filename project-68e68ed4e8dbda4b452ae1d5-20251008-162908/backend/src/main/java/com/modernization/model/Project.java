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
 * Represents a business project in the system.
 * Projects contain tasks, team members, and associated documents.
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
     * Default constructor for Spring Data MongoDB
     */
    public Project() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with required fields
     */
    public Project(String name, String status, LocalDate startDate, String ownerId) {
        this();
        this.name = name;
        this.status = status;
        this.startDate = startDate;
        this.ownerId = ownerId;
    }

    /**
     * Adds a team member to the project if they don't already exist
     * 
     * @param teamMember The team member to add
     * @return true if added successfully, false if already exists
     */
    public boolean addTeamMember(TeamMember teamMember) {
        if (teamMembers.stream().noneMatch(tm -> tm.getUserId().equals(teamMember.getUserId()))) {
            return teamMembers.add(teamMember);
        }
        return false;
    }

    /**
     * Removes a team member from the project
     * 
     * @param userId The ID of the user to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeTeamMember(String userId) {
        return teamMembers.removeIf(tm -> tm.getUserId().equals(userId));
    }

    /**
     * Updates the project's status and modified timestamp
     * 
     * @param status The new status
     */
    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the project is completed
     * 
     * @return true if status is "COMPLETED", false otherwise
     */
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status);
    }

    /**
     * Checks if the project is active
     * 
     * @return true if status is "ACTIVE", false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    /**
     * Calculates if the project is overdue based on end date
     * 
     * @return true if end date is in the past and project is not completed
     */
    public boolean isOverdue() {
        return endDate != null && 
               LocalDate.now().isAfter(endDate) && 
               !isCompleted();
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