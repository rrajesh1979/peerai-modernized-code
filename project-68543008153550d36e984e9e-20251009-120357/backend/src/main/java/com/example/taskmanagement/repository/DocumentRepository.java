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
     * @return a list of documents
     */
    List<Document> findByProjectId(String projectId);

    /**
     * Find all documents associated with a specific organization.
     *
     * @param organizationId the organization identifier
     * @return a list of documents
     */
    List<Document> findByOrganizationId(String organizationId);

    /**
     * Find documents by type.
     *
     * @param type the document type
     * @return a list of documents
     */
    List<Document> findByType(String type);

    /**
     * Find documents by type with pagination.
     *
     * @param type the document type
     * @param pageable pagination information
     * @return a page of documents
     */
    Page<Document> findByType(String type, Pageable pageable);

    /**
     * Find documents by project and type.
     *
     * @param projectId the project identifier
     * @param type the document type
     * @return a list of documents
     */
    List<Document> findByProjectIdAndType(String projectId, String type);

    /**
     * Find documents by organization and type.
     *
     * @param organizationId the organization identifier
     * @param type the document type
     * @return a list of documents
     */
    List<Document> findByOrganizationIdAndType(String organizationId, String type);

    /**
     * Search for documents by name containing the given text.
     *
     * @param nameText the text to search for in document names
     * @return a list of matching documents
     */
    List<Document> findByNameContainingIgnoreCase(String nameText);

    /**
     * Custom query to find documents by content text search.
     * Note: Requires a text index on the content field in MongoDB.
     *
     * @param searchText the text to search for in document content
     * @return a list of matching documents
     */
    @Query("{ '$text': { '$search': ?0 } }")
    List<Document> findByContentText(String searchText);

    /**
     * Find documents that are associated with a project and have no organization.
     *
     * @param projectId the project identifier
     * @return a list of documents
     */
    @Query("{ 'projectId': ?0, 'organizationId': { $exists: false } }")
    List<Document> findByProjectIdWithNoOrganization(String projectId);

    /**
     * Count documents by project.
     *
     * @param projectId the project identifier
     * @return the count of documents
     */
    long countByProjectId(String projectId);

    /**
     * Count documents by organization.
     *
     * @param organizationId the organization identifier
     * @return the count of documents
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