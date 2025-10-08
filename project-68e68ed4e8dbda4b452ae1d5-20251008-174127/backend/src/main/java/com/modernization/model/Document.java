package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Document entity representing business documents and files stored in the system.
 * Maps to the 'documents' collection in MongoDB.
 */
@Document(collection = "documents")
public class Document {

    @Id
    private String id;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name cannot exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotBlank(message = "File type is required")
    private String fileType;

    @NotNull(message = "File size is required")
    @Min(value = 0, message = "File size must be a positive number")
    private Long size;

    @NotNull(message = "Uploader information is required")
    private String uploadedBy;

    private String projectId;

    private List<String> tags = new ArrayList<>();

    @NotNull(message = "Version information is required")
    private Integer version;

    private Boolean isPublic = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastAccessedAt;

    /**
     * Default constructor
     */
    public Document() {
    }

    /**
     * Constructor with essential fields
     *
     * @param name       document name
     * @param fileUrl    URL to access the file
     * @param fileType   type of the file (e.g., PDF, DOCX)
     * @param size       file size in bytes
     * @param uploadedBy user ID who uploaded the document
     * @param version    document version
     */
    public Document(String name, String fileUrl, String fileType, Long size, String uploadedBy, Integer version) {
        this.name = name;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.size = size;
        this.uploadedBy = uploadedBy;
        this.version = version;
    }

    /**
     * Full constructor
     *
     * @param name          document name
     * @param description   document description
     * @param fileUrl       URL to access the file
     * @param fileType      type of the file
     * @param size          file size in bytes
     * @param uploadedBy    user ID who uploaded the document
     * @param projectId     associated project ID
     * @param tags          list of tags for categorization
     * @param version       document version
     * @param isPublic      whether the document is publicly accessible
     */
    public Document(String name, String description, String fileUrl, String fileType, Long size, 
                   String uploadedBy, String projectId, List<String> tags, Integer version, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.size = size;
        this.uploadedBy = uploadedBy;
        this.projectId = projectId;
        this.tags = tags;
        this.version = version;
        this.isPublic = isPublic;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    /**
     * Add a tag to the document
     *
     * @param tag the tag to add
     * @return true if the tag was added, false if it already existed
     */
    public boolean addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            return tags.add(tag);
        }
        return false;
    }

    /**
     * Remove a tag from the document
     *
     * @param tag the tag to remove
     * @return true if the tag was removed, false if it wasn't found
     */
    public boolean removeTag(String tag) {
        if (tags != null) {
            return tags.remove(tag);
        }
        return false;
    }

    /**
     * Update the last accessed timestamp to the current time
     */
    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Increment the document version
     *
     * @return the new version number
     */
    public Integer incrementVersion() {
        this.version = this.version + 1;
        return this.version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) &&
               Objects.equals(name, document.name) &&
               Objects.equals(fileUrl, document.fileUrl) &&
               Objects.equals(version, document.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fileUrl, version);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fileType='" + fileType + '\'' +
                ", size=" + size +
                ", version=" + version +
                ", projectId='" + projectId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}