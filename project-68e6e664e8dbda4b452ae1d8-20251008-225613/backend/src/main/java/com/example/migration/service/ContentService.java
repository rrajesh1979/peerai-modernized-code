package com.example.migration.service;

import com.example.migration.exception.ContentNotFoundException;
import com.example.migration.exception.InvalidContentException;
import com.example.migration.model.Content;
import com.example.migration.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing content operations.
 * Handles CRUD operations and business logic for content entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;

    /**
     * Retrieves all content with pagination support.
     *
     * @param pageable Pagination information
     * @return Page of content
     */
    public Page<Content> getAllContent(Pageable pageable) {
        log.debug("Retrieving content page: {}", pageable);
        return contentRepository.findAll(pageable);
    }

    /**
     * Retrieves content by its ID.
     *
     * @param id The content ID
     * @return The content entity
     * @throws ContentNotFoundException if content is not found
     */
    public Content getContentById(String id) {
        log.debug("Retrieving content with ID: {}", id);
        return contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with ID: " + id));
    }

    /**
     * Retrieves content by its slug.
     *
     * @param slug The content slug
     * @return The content entity
     * @throws ContentNotFoundException if content is not found
     */
    public Content getContentBySlug(String slug) {
        log.debug("Retrieving content with slug: {}", slug);
        return contentRepository.findBySlug(slug)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with slug: " + slug));
    }

    /**
     * Retrieves all content by author ID.
     *
     * @param authorId The author's ID
     * @return List of content by the author
     */
    public List<Content> getContentByAuthor(String authorId) {
        log.debug("Retrieving content by author ID: {}", authorId);
        return contentRepository.findByAuthorId(authorId);
    }

    /**
     * Retrieves all content by status.
     *
     * @param status The content status
     * @param pageable Pagination information
     * @return Page of content with the specified status
     */
    public Page<Content> getContentByStatus(String status, Pageable pageable) {
        log.debug("Retrieving content with status: {}, page: {}", status, pageable);
        return contentRepository.findByStatus(status, pageable);
    }

    /**
     * Retrieves all content by type.
     *
     * @param type The content type
     * @param pageable Pagination information
     * @return Page of content with the specified type
     */
    public Page<Content> getContentByType(String type, Pageable pageable) {
        log.debug("Retrieving content with type: {}, page: {}", type, pageable);
        return contentRepository.findByType(type, pageable);
    }

    /**
     * Retrieves all content by tag.
     *
     * @param tag The tag to search for
     * @param pageable Pagination information
     * @return Page of content with the specified tag
     */
    public Page<Content> getContentByTag(String tag, Pageable pageable) {
        log.debug("Retrieving content with tag: {}, page: {}", tag, pageable);
        return contentRepository.findByTagsContaining(tag, pageable);
    }

    /**
     * Retrieves all content by category.
     *
     * @param category The category to search for
     * @param pageable Pagination information
     * @return Page of content with the specified category
     */
    public Page<Content> getContentByCategory(String category, Pageable pageable) {
        log.debug("Retrieving content with category: {}, page: {}", category, pageable);
        return contentRepository.findByCategoriesContaining(category, pageable);
    }

    /**
     * Searches content by title or body.
     *
     * @param query The search query
     * @param pageable Pagination information
     * @return Page of content matching the search criteria
     */
    public Page<Content> searchContent(String query, Pageable pageable) {
        log.debug("Searching content with query: {}, page: {}", query, pageable);
        return contentRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(query, query, pageable);
    }

    /**
     * Creates a new content entity.
     *
     * @param content The content to create
     * @return The created content
     * @throws InvalidContentException if content validation fails
     */
    @Transactional
    public Content createContent(Content content) {
        validateContent(content);
        
        // Set creation timestamp if not provided
        if (content.getCreatedAt() == null) {
            content.setCreatedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());
        
        // Generate slug if not provided
        if (StringUtils.isBlank(content.getSlug())) {
            content.setSlug(generateSlug(content.getTitle()));
        }
        
        log.info("Creating new content with title: {}", content.getTitle());
        return contentRepository.save(content);
    }

    /**
     * Updates an existing content entity.
     *
     * @param id The content ID
     * @param content The updated content data
     * @return The updated content
     * @throws ContentNotFoundException if content is not found
     * @throws InvalidContentException if content validation fails
     */
    @Transactional
    public Content updateContent(String id, Content content) {
        log.debug("Updating content with ID: {}", id);
        
        Content existingContent = getContentById(id);
        validateContent(content);
        
        // Update fields
        existingContent.setTitle(content.getTitle());
        existingContent.setBody(content.getBody());
        existingContent.setSummary(content.getSummary());
        existingContent.setStatus(content.getStatus());
        existingContent.setType(content.getType());
        existingContent.setTags(content.getTags());
        existingContent.setCategories(content.getCategories());
        existingContent.setFeaturedImage(content.getFeaturedImage());
        existingContent.setMetadata(content.getMetadata());
        existingContent.setUpdatedAt(LocalDateTime.now());
        
        // Update slug if title changed and slug was auto-generated
        if (content.getTitle() != null && !content.getTitle().equals(existingContent.getTitle()) && 
            existingContent.getSlug().equals(generateSlug(existingContent.getTitle()))) {
            existingContent.setSlug(generateSlug(content.getTitle()));
        }
        
        // If slug is explicitly provided, use it
        if (content.getSlug() != null && !content.getSlug().equals(existingContent.getSlug())) {
            existingContent.setSlug(content.getSlug());
        }
        
        log.info("Updated content with ID: {}", id);
        return contentRepository.save(existingContent);
    }

    /**
     * Updates the status of a content entity.
     *
     * @param id The content ID
     * @param status The new status
     * @return The updated content
     * @throws ContentNotFoundException if content is not found
     */
    @Transactional
    public Content updateContentStatus(String id, String status) {
        log.debug("Updating status of content ID: {} to {}", id, status);
        
        Content content = getContentById(id);
        content.setStatus(status);
        content.setUpdatedAt(LocalDateTime.now());
        
        log.info("Updated status of content ID: {} to {}", id, status);
        return contentRepository.save(content);
    }

    /**
     * Deletes a content entity.
     *
     * @param id The content ID
     * @throws ContentNotFoundException if content is not found
     */
    @Transactional
    public void deleteContent(String id) {
        log.debug("Deleting content with ID: {}", id);
        
        if (!contentRepository.existsById(id)) {
            throw new ContentNotFoundException("Content not found with ID: " + id);
        }
        
        contentRepository.deleteById(id);
        log.info("Deleted content with ID: {}", id);
    }

    /**
     * Validates content data before saving.
     *
     * @param content The content to validate
     * @throws InvalidContentException if validation fails
     */
    private void validateContent(Content content) {
        if (content == null) {
            throw new InvalidContentException("Content cannot be null");
        }
        
        if (StringUtils.isBlank(content.getTitle())) {
            throw new InvalidContentException("Content title is required");
        }
        
        if (StringUtils.isBlank(content.getBody())) {
            throw new InvalidContentException("Content body is required");
        }
        
        if (StringUtils.isBlank(content.getType())) {
            throw new InvalidContentException("Content type is required");
        }
        
        if (StringUtils.isBlank(content.getAuthorId())) {
            throw new InvalidContentException("Content author is required");
        }
        
        // Check for duplicate slug if provided
        if (StringUtils.isNotBlank(content.getSlug()) && content.getId() == null) {
            Optional<Content> existingContent = contentRepository.findBySlug(content.getSlug());
            if (existingContent.isPresent()) {
                throw new InvalidContentException("Content with slug '" + content.getSlug() + "' already exists");
            }
        }
    }

    /**
     * Generates a URL-friendly slug from a title.
     *
     * @param title The title to convert to a slug
     * @return The generated slug
     */
    private String generateSlug(String title) {
        if (StringUtils.isBlank(title)) {
            return "";
        }
        
        // Convert to lowercase, replace spaces with hyphens, remove special characters
        String slug = title.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-+", "-");
        
        // Check if slug already exists and append a number if needed
        String baseSlug = slug;
        int counter = 1;
        
        while (contentRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}