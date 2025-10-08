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
     * @param projectId the project ID
     * @return list of documents for the project
     */
    List<Document> findByProjectId(String projectId);

    /**
     * Find all documents associated with a specific organization.
     *
     * @param organizationId the organization ID
     * @return list of documents for the organization
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
     * @param projectId the project ID
     * @param pageable pagination information
     * @return page of documents for the project
     */
    Page<Document> findByProjectId(String projectId, Pageable pageable);

    /**
     * Find documents by organization ID with pagination.
     *
     * @param organizationId the organization ID
     * @param pageable pagination information
     * @return page of documents for the organization
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
     * @param projectId the project ID
     * @param type the document type
     * @return list of documents matching both criteria
     */
    List<Document> findByProjectIdAndType(String projectId, String type);

    /**
     * Find documents by organization ID and type.
     *
     * @param organizationId the organization ID
     * @param type the document type
     * @return list of documents matching both criteria
     */
    List<Document> findByOrganizationIdAndType(String organizationId, String type);

    /**
     * Custom query to find documents by content text.
     * Uses MongoDB text search capabilities.
     *
     * @param searchText the text to search for in document content
     * @return list of documents containing the search text
     */
    @Query("{ '$text': { '$search': ?0 } }")
    List<Document> findByContentText(String searchText);

    /**
     * Check if a document with the given name already exists.
     *
     * @param name the document name to check
     * @return true if a document with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Count documents by project ID.
     *
     * @param projectId the project ID
     * @return count of documents for the project
     */
    long countByProjectId(String projectId);

    /**
     * Count documents by organization ID.
     *
     * @param organizationId the organization ID
     * @return count of documents for the organization
     */
    long countByOrganizationId(String organizationId);

    /**
     * Delete all documents associated with a project.
     *
     * @param projectId the project ID
     */
    void deleteByProjectId(String projectId);

    /**
     * Delete all documents associated with an organization.
     *
     * @param organizationId the organization ID
     */
    void deleteByOrganizationId(String organizationId);
}