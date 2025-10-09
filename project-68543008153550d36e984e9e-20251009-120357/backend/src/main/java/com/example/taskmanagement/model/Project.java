package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project entity representing a project within an organization.
 * Projects contain tasks and can have associated documents.
 */
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @NotBlank(message = "Project name is required")
    @Indexed
    private String name;

    private String description;

    @NotNull(message = "Organization ID is required")
    @Indexed
    private String organizationId;

    @Field("startDate")
    private LocalDateTime startDate;

    @Field("endDate")
    private LocalDateTime endDate;

    @Field("status")
    private ProjectStatus status = ProjectStatus.PLANNING;

    @Field("budget")
    private Double budget;

    @Field("teamMembers")
    private List<String> teamMemberIds = new ArrayList<>();

    @Field("tags")
    private List<String> tags = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Enum representing the possible statuses of a project
     */
    public enum ProjectStatus {
        PLANNING,
        ACTIVE,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }

    // Default constructor required by MongoDB
    public Project() {
    }

    /**
     * Constructor with required fields
     * 
     * @param name          the project name
     * @param organizationId the ID of the organization this project belongs to
     */
    public Project(String name, String organizationId) {
        this.name = name;
        this.organizationId = organizationId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Full constructor
     */
    public Project(String name, String description, String organizationId, 
                  LocalDateTime startDate, LocalDateTime endDate, 
                  ProjectStatus status, Double budget) {
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.budget = budget;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<String> getTeamMemberIds() {
        return teamMemberIds;
    }

    public void setTeamMemberIds(List<String> teamMemberIds) {
        this.teamMemberIds = teamMemberIds;
    }

    /**
     * Add a team member to the project
     * 
     * @param userId the ID of the user to add
     * @return true if the user was added, false if already present
     */
    public boolean addTeamMember(String userId) {
        if (!teamMemberIds.contains(userId)) {
            return teamMemberIds.add(userId);
        }
        return false;
    }

    /**
     * Remove a team member from the project
     * 
     * @param userId the ID of the user to remove
     * @return true if the user was removed, false if not found
     */
    public boolean removeTeamMember(String userId) {
        return teamMemberIds.remove(userId);
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Add a tag to the project
     * 
     * @param tag the tag to add
     * @return true if the tag was added, false if already present
     */
    public boolean addTag(String tag) {
        if (!tags.contains(tag)) {
            return tags.add(tag);
        }
        return false;
    }

    /**
     * Remove a tag from the project
     * 
     * @param tag the tag to remove
     * @return true if the tag was removed, false if not found
     */
    public boolean removeTag(String tag) {
        return tags.remove(tag);
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
        return Objects.equals(id, project.id) &&
               Objects.equals(name, project.name) &&
               Objects.equals(organizationId, project.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, organizationId);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}