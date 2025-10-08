package com.example.migration.repository;

import com.example.migration.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Content document operations.
 * Provides methods to interact with the Content collection in MongoDB.
 */
@Repository
public interface ContentRepository extends MongoRepository<Content, String> {

    /**
     * Find content by its unique slug
     * @param slug The URL-friendly identifier for the content
     * @return Optional containing the content if found
     */
    Optional<Content> findBySlug(String slug);
    
    /**
     * Find all content by author ID
     * @param authorId The ID of the author
     * @return List of content created by the specified author
     */
    List<Content> findByAuthorId(String authorId);
    
    /**
     * Find all content by status
     * @param status The publication status (e.g., "PUBLISHED", "DRAFT", "ARCHIVED")
     * @param pageable Pagination information
     * @return Page of content with the specified status
     */
    Page<Content> findByStatus(String status, Pageable pageable);
    
    /**
     * Find all content by type
     * @param type The content type (e.g., "ARTICLE", "PAGE", "DOCUMENT")
     * @param pageable Pagination information
     * @return Page of content with the specified type
     */
    Page<Content> findByType(String type, Pageable pageable);
    
    /**
     * Find all content containing a specific tag
     * @param tag The tag to search for
     * @param pageable Pagination information
     * @return Page of content containing the specified tag
     */
    Page<Content> findByTagsContaining(String tag, Pageable pageable);
    
    /**
     * Find all content belonging to a specific category
     * @param category The category to search for
     * @param pageable Pagination information
     * @return Page of content belonging to the specified category
     */
    Page<Content> findByCategoriesContaining(String category, Pageable pageable);
    
    /**
     * Find all published content created after a specific date
     * @param date The date threshold
     * @param pageable Pagination information
     * @return Page of published content created after the specified date
     */
    @Query("{'status': 'PUBLISHED', 'createdAt': {$gt: ?0}}")
    Page<Content> findPublishedContentCreatedAfter(LocalDateTime date, Pageable pageable);
    
    /**
     * Search for content by title or body containing the search term
     * @param searchTerm The term to search for
     * @param pageable Pagination information
     * @return Page of content matching the search criteria
     */
    @Query("{'$or': [{'title': {$regex: ?0, $options: 'i'}}, {'body': {$regex: ?0, $options: 'i'}}]}")
    Page<Content> searchByTitleOrBody(String searchTerm, Pageable pageable);
    
    /**
     * Find featured content
     * @param pageable Pagination information
     * @return Page of featured content
     */
    Page<Content> findByFeaturedTrue(Pageable pageable);
    
    /**
     * Count content by status
     * @param status The publication status
     * @return Count of content with the specified status
     */
    long countByStatus(String status);
    
    /**
     * Find content that needs review (draft content older than the specified date)
     * @param date The date threshold
     * @return List of content needing review
     */
    @Query("{'status': 'DRAFT', 'updatedAt': {$lt: ?0}}")
    List<Content> findContentNeedingReview(LocalDateTime date);
    
    /**
     * Find content by author ID and status
     * @param authorId The ID of the author
     * @param status The publication status
     * @param pageable Pagination information
     * @return Page of content matching both criteria
     */
    Page<Content> findByAuthorIdAndStatus(String authorId, String status, Pageable pageable);
}