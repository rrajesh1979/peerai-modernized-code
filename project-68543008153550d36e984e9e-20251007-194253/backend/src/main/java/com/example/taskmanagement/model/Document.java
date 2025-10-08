package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Document entity representing project and organization documents stored in MongoDB.
 * Documents can be associated with either a project or an organization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class Document {

    @Id
    private String id;

    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "Document type is required")
    private String type;

    private String projectId;

    private String organizationId;

    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String fileUrl;

    private Long fileSize;

    private String contentType;

    @Field("metadata")
    private Map<String, Object> metadata;

    private String description;

    /**
     * Validates that the document is associated with either a project or an organization, but not both.
     * This method should be called before saving the document.
     *
     * @return true if the document has valid associations
     * @throws IllegalStateException if the document has invalid associations
     */
    public boolean validateAssociations() {
        if (projectId != null && organizationId != null) {
            throw new IllegalStateException("Document cannot be associated with both a project and an organization");
        }
        if (projectId == null && organizationId == null) {
            throw new IllegalStateException("Document must be associated with either a project or an organization");
        }
        return true;
    }

    /**
     * Checks if this document is associated with a project.
     *
     * @return true if the document is associated with a project
     */
    public boolean isProjectDocument() {
        return projectId != null;
    }

    /**
     * Checks if this document is associated with an organization.
     *
     * @return true if the document is associated with an organization
     */
    public boolean isOrganizationDocument() {
        return organizationId != null;
    }
}