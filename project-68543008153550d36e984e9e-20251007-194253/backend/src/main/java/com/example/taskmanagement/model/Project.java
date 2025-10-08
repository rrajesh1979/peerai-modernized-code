package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a project entity in the task management system.
 * Projects belong to organizations and can contain multiple tasks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Builder.Default
    private List<String> memberIds = new ArrayList<>();

    @Builder.Default
    private List<String> managerIds = new ArrayList<>();

    private LocalDateTime startDate;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime completedDate;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private String priority;

    private Double budget;

    @Field("metadata")
    @Builder.Default
    private ProjectMetadata metadata = new ProjectMetadata();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Enum representing the possible states of a project
     */
    public enum ProjectStatus {
        PLANNING,
        ACTIVE,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }

    /**
     * Nested class for storing additional project metadata
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectMetadata {
        private String clientName;
        private String clientContact;
        private String department;
        private String category;
        
        @Builder.Default
        private List<String> relatedProjects = new ArrayList<>();
        
        @Builder.Default
        private List<String> externalLinks = new ArrayList<>();
    }

    /**
     * Adds a member to the project if not already present
     * 
     * @param userId the ID of the user to add as a member
     * @return true if the member was added, false if already present
     */
    public boolean addMember(String userId) {
        if (!memberIds.contains(userId)) {
            return memberIds.add(userId);
        }
        return false;
    }

    /**
     * Removes a member from the project
     * 
     * @param userId the ID of the user to remove
     * @return true if the member was removed, false if not found
     */
    public boolean removeMember(String userId) {
        return memberIds.remove(userId);
    }

    /**
     * Adds a manager to the project if not already present
     * 
     * @param userId the ID of the user to add as a manager
     * @return true if the manager was added, false if already present
     */
    public boolean addManager(String userId) {
        if (!managerIds.contains(userId)) {
            return managerIds.add(userId);
        }
        return false;
    }

    /**
     * Removes a manager from the project
     * 
     * @param userId the ID of the user to remove
     * @return true if the manager was removed, false if not found
     */
    public boolean removeManager(String userId) {
        return managerIds.remove(userId);
    }

    /**
     * Marks the project as completed with the current timestamp
     */
    public void markAsCompleted() {
        this.status = ProjectStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
    }

    /**
     * Checks if the project is overdue based on its due date
     * 
     * @return true if the project has a due date and it's in the past
     */
    public boolean isOverdue() {
        return dueDate != null && 
               LocalDateTime.now().isAfter(dueDate) && 
               status != ProjectStatus.COMPLETED && 
               status != ProjectStatus.CANCELLED;
    }
}