package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Document entity operations.
 * Provides methods to interact with the Documents collection in MongoDB.
 */
@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {

    /**
     * Find a document by its name.
     *
     * @param name the document name
     * @return an Optional containing the document if found
     */
    Optional<Document> findByName(String name);

    /**
     * Find all documents associated with a specific project.
     *
     * @param projectId the project identifier
     * @return list of documents belonging to the project
     */
    List<Document> findByProjectId(String projectId);

    /**
     * Find all documents associated with a specific organization.
     *
     * @param organizationId the organization identifier
     * @return list of documents belonging to the organization
     */
    List<Document> findByOrganizationId(String organizationId);

    /**
     * Find documents by type.
     *
     * @param type the document type
     * @return list of documents of the specified type
     */
    List<Document> findByType(String type);

    /**
     * Find documents by type with pagination.
     *
     * @param type the document type
     * @param pageable pagination information
     * @return page of documents of the specified type
     */
    Page<Document> findByType(String type, Pageable pageable);

    /**
     * Find documents by project ID with pagination.
     *
     * @param projectId the project identifier
     * @param pageable pagination information
     * @return page of documents belonging to the project
     */
    Page<Document> findByProjectId(String projectId, Pageable pageable);

    /**
     * Find documents by organization ID with pagination.
     *
     * @param organizationId the organization identifier
     * @param pageable pagination information
     * @return page of documents belonging to the organization
     */
    Page<Document> findByOrganizationId(String organizationId, Pageable pageable);

    /**
     * Search for documents by name containing the given text.
     *
     * @param nameText the text to search for in document names
     * @return list of documents with names containing the search text
     */
    List<Document> findByNameContainingIgnoreCase(String nameText);

    /**
     * Find documents by project ID and type.
     *
     * @param projectId the project identifier
     * @param type the document type
     * @return list of documents matching both criteria
     */
    List<Document> findByProjectIdAndType(String projectId, String type);

    /**
     * Find documents by organization ID and type.
     *
     * @param organizationId the organization identifier
     * @param type the document type
     * @return list of documents matching both criteria
     */
    List<Document> findByOrganizationIdAndType(String organizationId, String type);

    /**
     * Custom query to find documents by content containing the given text.
     * Uses MongoDB text search capabilities.
     *
     * @param searchText the text to search for in document content
     * @return list of documents containing the search text
     */
    @Query("{ '$text': { '$search': ?0 } }")
    List<Document> findByContentText(String searchText);

    /**
     * Count documents by project ID.
     *
     * @param projectId the project identifier
     * @return count of documents associated with the project
     */
    long countByProjectId(String projectId);

    /**
     * Count documents by organization ID.
     *
     * @param organizationId the organization identifier
     * @return count of documents associated with the organization
     */
    long countByOrganizationId(String organizationId);

    /**
     * Delete all documents associated with a project.
     *
     * @param projectId the project identifier
     */
    void deleteByProjectId(String projectId);

    /**
     * Delete all documents associated with an organization.
     *
     * @param organizationId the organization identifier
     */
    void deleteByOrganizationId(String organizationId);
}