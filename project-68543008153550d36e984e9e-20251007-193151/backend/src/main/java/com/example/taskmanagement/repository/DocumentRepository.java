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
 * Repository interface for Document entity operations with MongoDB.
 * Provides methods for CRUD operations and custom queries for Document entities.
 */
@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {

    /**
     * Find a document by its name.
     *
     * @param name the name of the document
     * @return an Optional containing the document if found
     */
    Optional<Document> findByName(String name);

    /**
     * Find all documents associated with a specific project.
     *
     * @param projectId the ID of the project
     * @return a list of documents associated with the project
     */
    List<Document> findByProjectId(String projectId);

    /**
     * Find all documents associated with a specific organization.
     *
     * @param organizationId the ID of the organization
     * @return a list of documents associated with the organization
     */
    List<Document> findByOrganizationId(String organizationId);

    /**
     * Find all documents of a specific type.
     *
     * @param type the document type
     * @return a list of documents of the specified type
     */
    List<Document> findByType(String type);

    /**
     * Find documents by type with pagination support.
     *
     * @param type the document type
     * @param pageable pagination information
     * @return a page of documents of the specified type
     */
    Page<Document> findByType(String type, Pageable pageable);

    /**
     * Find documents associated with a specific project with pagination support.
     *
     * @param projectId the ID of the project
     * @param pageable pagination information
     * @return a page of documents associated with the project
     */
    Page<Document> findByProjectId(String projectId, Pageable pageable);

    /**
     * Find documents associated with a specific organization with pagination support.
     *
     * @param organizationId the ID of the organization
     * @param pageable pagination information
     * @return a page of documents associated with the organization
     */
    Page<Document> findByOrganizationId(String organizationId, Pageable pageable);

    /**
     * Search for documents by name containing the given text.
     *
     * @param nameText the text to search for in document names
     * @return a list of documents with names containing the search text
     */
    List<Document> findByNameContainingIgnoreCase(String nameText);

    /**
     * Find documents created after a specific date.
     *
     * @param date the date to compare against
     * @return a list of documents created after the specified date
     */
    @Query("{'createdAt': {$gt: ?0}}")
    List<Document> findByCreatedAtAfter(java.util.Date date);

    /**
     * Find documents by project ID and type.
     *
     * @param projectId the ID of the project
     * @param type the document type
     * @return a list of documents matching both criteria
     */
    List<Document> findByProjectIdAndType(String projectId, String type);

    /**
     * Find documents by organization ID and type.
     *
     * @param organizationId the ID of the organization
     * @param type the document type
     * @return a list of documents matching both criteria
     */
    List<Document> findByOrganizationIdAndType(String organizationId, String type);

    /**
     * Count documents by project ID.
     *
     * @param projectId the ID of the project
     * @return the count of documents associated with the project
     */
    long countByProjectId(String projectId);

    /**
     * Count documents by organization ID.
     *
     * @param organizationId the ID of the organization
     * @return the count of documents associated with the organization
     */
    long countByOrganizationId(String organizationId);

    /**
     * Delete all documents associated with a specific project.
     *
     * @param projectId the ID of the project
     */
    void deleteByProjectId(String projectId);

    /**
     * Delete all documents associated with a specific organization.
     *
     * @param organizationId the ID of the organization
     */
    void deleteByOrganizationId(String organizationId);
}