package com.example.migration.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents application content such as pages, articles, and documents.
 * This entity maps to the 'Content' collection in MongoDB.
 */
@Document(collection = "Content")
public class Content {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 255, message = "Slug cannot exceed 255 characters")
    @Indexed(unique = true)
    private String slug;

    @NotBlank(message = "Body content is required")
    private String body;

    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    private String authorId;

    @NotNull(message = "Status is required")
    private ContentStatus status = ContentStatus.DRAFT;

    @NotNull(message = "Content type is required")
    private ContentType type;

    private List<String> tags = new ArrayList<>();

    private List<String> categories = new ArrayList<>();

    private String featuredImage;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    private Integer viewCount = 0;

    private Integer likeCount = 0;

    private Integer commentCount = 0;

    private Boolean allowComments = true;

    private Boolean isFeatured = false;

    private String metaTitle;

    private String metaDescription;

    private List<String> relatedContentIds = new ArrayList<>();

    /**
     * Enum representing the possible statuses of content
     */
    public enum ContentStatus {
        DRAFT, PUBLISHED, ARCHIVED, SCHEDULED, UNDER_REVIEW
    }

    /**
     * Enum representing the possible types of content
     */
    public enum ContentType {
        PAGE, ARTICLE, BLOG_POST, NEWS, DOCUMENT, FAQ
    }

    // Default constructor
    public Content() {
    }

    // Constructor with required fields
    public Content(String title, String slug, String body, ContentType type, String authorId) {
        this.title = title;
        this.slug = slug;
        this.body = body;
        this.type = type;
        this.authorId = authorId;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getAllowComments() {
        return allowComments;
    }

    public void setAllowComments(Boolean allowComments) {
        this.allowComments = allowComments;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public List<String> getRelatedContentIds() {
        return relatedContentIds;
    }

    public void setRelatedContentIds(List<String> relatedContentIds) {
        this.relatedContentIds = relatedContentIds;
    }

    /**
     * Increment the view count by 1
     */
    public void incrementViewCount() {
        this.viewCount = this.viewCount + 1;
    }

    /**
     * Increment the like count by 1
     */
    public void incrementLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    /**
     * Decrement the like count by 1
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount = this.likeCount - 1;
        }
    }

    /**
     * Update comment count based on new comments or deleted comments
     * 
     * @param count The number to adjust the comment count by (positive or negative)
     */
    public void updateCommentCount(int count) {
        this.commentCount = Math.max(0, this.commentCount + count);
    }

    /**
     * Publish the content by setting status to PUBLISHED and recording the publish time
     */
    public void publish() {
        this.status = ContentStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}