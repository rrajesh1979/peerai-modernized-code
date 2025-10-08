package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project entity representing a project in the task management system.
 * Projects belong to an organization and can contain multiple tasks.
 */
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Organization ID is required")
    @Indexed
    private String organizationId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private ProjectStatus status = ProjectStatus.ACTIVE;
    
    private List<String> memberIds = new ArrayList<>();
    
    private String managerId;
    
    private Double budget;
    
    private String category;
    
    private List<String> tags = new ArrayList<>();

    /**
     * Enum representing the possible statuses of a project
     */
    public enum ProjectStatus {
        PLANNING, ACTIVE, ON_HOLD, COMPLETED, CANCELLED
    }

    /**
     * Default constructor
     */
    public Project() {
    }

    /**
     * Constructor with required fields
     * 
     * @param name Project name
     * @param organizationId ID of the organization this project belongs to
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
                  ProjectStatus status, String managerId, Double budget, 
                  String category) {
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.managerId = managerId;
        this.budget = budget;
        this.category = category;
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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Add a member to the project
     * 
     * @param memberId ID of the user to add as a member
     * @return true if the member was added, false if already a member
     */
    public boolean addMember(String memberId) {
        if (!memberIds.contains(memberId)) {
            return memberIds.add(memberId);
        }
        return false;
    }

    /**
     * Remove a member from the project
     * 
     * @param memberId ID of the user to remove
     * @return true if the member was removed, false if not a member
     */
    public boolean removeMember(String memberId) {
        return memberIds.remove(memberId);
    }

    /**
     * Add a tag to the project
     * 
     * @param tag Tag to add
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
     * @param tag Tag to remove
     * @return true if the tag was removed, false if not present
     */
    public boolean removeTag(String tag) {
        return tags.remove(tag);
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
                ", organizationId='" + organizationId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}