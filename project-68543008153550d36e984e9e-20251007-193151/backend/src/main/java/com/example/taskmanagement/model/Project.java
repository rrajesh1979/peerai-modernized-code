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
 * Project entity representing a project in the task management system.
 * Projects belong to an organization and can contain multiple tasks.
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
    @Field("organizationId")
    private String organizationId;

    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Builder.Default
    private List<String> memberIds = new ArrayList<>();

    @Builder.Default
    private List<String> managerIds = new ArrayList<>();

    private LocalDateTime startDate;
    
    private LocalDateTime dueDate;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    private String priority;
    
    private Double budget;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
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
     * Checks if a user is a member of this project
     * 
     * @param userId the ID of the user to check
     * @return true if the user is a member, false otherwise
     */
    public boolean isMember(String userId) {
        return memberIds.contains(userId);
    }
    
    /**
     * Checks if a user is a manager of this project
     * 
     * @param userId the ID of the user to check
     * @return true if the user is a manager, false otherwise
     */
    public boolean isManager(String userId) {
        return managerIds.contains(userId);
    }
    
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
}