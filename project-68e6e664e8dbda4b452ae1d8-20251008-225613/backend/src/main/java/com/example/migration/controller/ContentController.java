package com.example.migration.controller;

import com.example.migration.dto.ContentDTO;
import com.example.migration.dto.ContentSummaryDTO;
import com.example.migration.dto.request.ContentCreateRequest;
import com.example.migration.dto.request.ContentUpdateRequest;
import com.example.migration.dto.response.ApiResponse;
import com.example.migration.dto.response.PagedResponse;
import com.example.migration.service.ContentService;
import com.example.migration.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@Slf4j
public class ContentController {

    private final ContentService contentService;

    /**
     * Create new content
     *
     * @param request Content creation request
     * @return Created content
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse<ContentDTO>> createContent(
            @Valid @RequestBody ContentCreateRequest request) {
        log.info("Creating new content with title: {}", request.getTitle());
        ContentDTO createdContent = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Content created successfully", createdContent));
    }

    /**
     * Get content by ID
     *
     * @param id Content ID
     * @return Content details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDTO>> getContentById(@PathVariable String id) {
        log.info("Fetching content with id: {}", id);
        ContentDTO content = contentService.getContentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Content retrieved successfully", content));
    }

    /**
     * Get content by slug
     *
     * @param slug Content slug
     * @return Content details
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ContentDTO>> getContentBySlug(@PathVariable String slug) {
        log.info("Fetching content with slug: {}", slug);
        ContentDTO content = contentService.getContentBySlug(slug);
        return ResponseEntity.ok(new ApiResponse<>(true, "Content retrieved successfully", content));
    }

    /**
     * Get all content with pagination
     *
     * @param page     Page number (0-based)
     * @param size     Page size
     * @param sortBy   Field to sort by
     * @param sortDir  Sort direction (asc/desc)
     * @param type     Content type filter (optional)
     * @param status   Content status filter (optional)
     * @param category Content category filter (optional)
     * @param tag      Content tag filter (optional)
     * @return Paged list of content summaries
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ContentSummaryDTO>>> getAllContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {
        
        log.info("Fetching content page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PagedResponse<ContentSummaryDTO> contentList = contentService.getAllContent(
                pageable, type, status, category, tag);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Content retrieved successfully", contentList));
    }

    /**
     * Update existing content
     *
     * @param id      Content ID
     * @param request Content update request
     * @return Updated content
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse<ContentDTO>> updateContent(
            @PathVariable String id,
            @Valid @RequestBody ContentUpdateRequest request) {
        log.info("Updating content with id: {}", id);
        ContentDTO updatedContent = contentService.updateContent(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Content updated successfully", updatedContent));
    }

    /**
     * Delete content by ID
     *
     * @param id Content ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable String id) {
        log.info("Deleting content with id: {}", id);
        contentService.deleteContent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Content deleted successfully", null));
    }

    /**
     * Change content status
     *
     * @param id     Content ID
     * @param status New status
     * @return Updated content
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<ContentDTO>> updateContentStatus(
            @PathVariable String id,
            @RequestParam String status) {
        log.info("Updating status of content id: {} to {}", id, status);
        
        if (!Constants.VALID_CONTENT_STATUSES.contains(status)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Invalid content status: " + status, null));
        }
        
        ContentDTO updatedContent = contentService.updateContentStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Content status updated successfully", updatedContent));
    }

    /**
     * Get content by author ID
     *
     * @param authorId Author ID
     * @param page     Page number (0-based)
     * @param size     Page size
     * @return Paged list of content summaries by author
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<PagedResponse<ContentSummaryDTO>>> getContentByAuthor(
            @PathVariable String authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching content by author: {}, page: {}, size: {}", authorId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<ContentSummaryDTO> contentList = contentService.getContentByAuthor(authorId, pageable);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Content retrieved successfully", contentList));
    }

    /**
     * Search content by keyword
     *
     * @param query    Search query
     * @param page     Page number (0-based)
     * @param size     Page size
     * @return Paged list of content summaries matching search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ContentSummaryDTO>>> searchContent(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching content with query: {}, page: {}, size: {}", query, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<ContentSummaryDTO> searchResults = contentService.searchContent(query, pageable);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Search completed successfully", searchResults));
    }
}