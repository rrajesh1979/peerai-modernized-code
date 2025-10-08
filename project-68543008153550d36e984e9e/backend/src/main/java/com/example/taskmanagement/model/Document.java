package com.example.taskmanagement.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Document entity representing project and organization documents stored in MongoDB.
 * Documents can be associated with either a project or an organization.
 */
@Document(collection = "documents")
public class Document {

    @Id
    private String id;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name cannot exceed 255 characters")
    @Indexed
    private String name;

    @NotBlank(message = "Document type is required")
    private String type;

    @Field("projectId")
    private String projectId;

    @Field("organizationId")
    private String organizationId;

    @Field("fileUrl")
    private String fileUrl;

    @Field("fileSize")
    private Long fileSize;

    @Field("mimeType")
    private String mimeType;

    @Field("createdBy")
    private String createdBy;

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("description")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Field("version")
    private Integer version = 1;

    /**
     * Default constructor for Document
     */
    public Document() {
    }

    /**
     * Constructor with required fields
     * 
     * @param name the document name
     * @param type the document type
     */
    public Document(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Full constructor for Document
     * 
     * @param name           the document name
     * @param type           the document type
     * @param projectId      the associated project ID (optional)
     * @param organizationId the associated organization ID (optional)
     * @param fileUrl        the URL to the stored file
     * @param fileSize       the size of the file in bytes
     * @param mimeType       the MIME type of the file
     * @param createdBy      the user ID who created the document
     * @param description    the document description
     */
    public Document(String name, String type, String projectId, String organizationId, 
                   String fileUrl, Long fileSize, String mimeType, String createdBy, String description) {
        this.name = name;
        this.type = type;
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.createdBy = createdBy;
        this.description = description;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Increments the document version
     */
    public void incrementVersion() {
        this.version = this.version + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", projectId='" + projectId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileSize=" + fileSize +
                ", mimeType='" + mimeType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}