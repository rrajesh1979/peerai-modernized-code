package com.jsf.migration.repository;

import com.jsf.migration.model.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Form entity operations.
 * Provides methods to interact with the Forms collection in MongoDB.
 */
@Repository
public interface FormRepository extends MongoRepository<Form, String> {

    /**
     * Find a form by its name.
     *
     * @param name the form name to search for
     * @return an Optional containing the form if found
     */
    Optional<Form> findByName(String name);

    /**
     * Find forms containing the specified text in their name or description.
     *
     * @param searchText the text to search for
     * @return a list of matching forms
     */
    List<Form> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchText, String sameSearchText);

    /**
     * Find all active forms.
     *
     * @return a list of active forms
     */
    List<Form> findByActiveTrue();

    /**
     * Find forms by field type.
     * This query searches for forms that contain at least one field of the specified type.
     *
     * @param fieldType the field type to search for
     * @return a list of forms containing the specified field type
     */
    @Query("{'fields.type': ?0}")
    List<Form> findByFieldType(String fieldType);

    /**
     * Find forms that require a specific field.
     * This query searches for forms that contain a required field with the specified name.
     *
     * @param fieldName the name of the required field
     * @return a list of forms with the specified required field
     */
    @Query("{'fields': {$elemMatch: {'name': ?0, 'required': true}}}")
    List<Form> findByRequiredField(String fieldName);

    /**
     * Count forms associated with a specific workflow.
     *
     * @param workflowId the ID of the workflow
     * @return the count of forms associated with the workflow
     */
    long countByWorkflowId(String workflowId);

    /**
     * Find forms created after a specific date.
     *
     * @param timestamp the timestamp to compare against
     * @return a list of forms created after the specified date
     */
    List<Form> findByCreatedAtAfter(java.util.Date timestamp);

    /**
     * Find forms by creator user ID.
     *
     * @param userId the ID of the user who created the forms
     * @return a list of forms created by the specified user
     */
    List<Form> findByCreatedBy(String userId);

    /**
     * Find forms that have a specific validation type on any field.
     *
     * @param validationType the validation type to search for
     * @return a list of forms with the specified validation type
     */
    @Query("{'fields.validation.type': ?0}")
    List<Form> findByValidationType(String validationType);
}