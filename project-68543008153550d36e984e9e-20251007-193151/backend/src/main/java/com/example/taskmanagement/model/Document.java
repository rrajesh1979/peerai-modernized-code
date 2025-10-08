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
import java.util.Optional;
import org.bson.types.ObjectId;

/**
 * Document entity representing files and documents stored in the system.
 * Documents can be associated with projects or organizations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class Document {

    @Id
    private ObjectId id;

    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "Document type is required")
    private String type;

    private String description;

    @Field("projectId")
    private ObjectId projectId;

    @Field("organizationId")
    private ObjectId organizationId;

    @Field("createdBy")
    private ObjectId createdBy;

    @Field("fileSize")
    private Long fileSize;

    @Field("fileUrl")
    private String fileUrl;

    @Field("contentType")
    private String contentType;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Validates that the document is associated with either a project or an organization,
     * but not both or neither.
     *
     * @return true if the document has valid associations
     */
    public boolean hasValidAssociation() {
        return (projectId != null && organizationId == null) || 
               (projectId == null && organizationId != null);
    }

    /**
     * Gets the entity type this document is associated with.
     *
     * @return "PROJECT" or "ORGANIZATION" based on the association
     * @throws IllegalStateException if the document has invalid associations
     */
    public String getAssociatedEntityType() {
        if (projectId != null && organizationId == null) {
            return "PROJECT";
        } else if (projectId == null && organizationId != null) {
            return "ORGANIZATION";
        } else {
            throw new IllegalStateException("Document must be associated with either a project or an organization");
        }
    }

    /**
     * Gets the ID of the entity this document is associated with.
     *
     * @return the ID of the associated project or organization
     * @throws IllegalStateException if the document has invalid associations
     */
    public ObjectId getAssociatedEntityId() {
        if (projectId != null && organizationId == null) {
            return projectId;
        } else if (projectId == null && organizationId != null) {
            return organizationId;
        } else {
            throw new IllegalStateException("Document must be associated with either a project or an organization");
        }
    }
}