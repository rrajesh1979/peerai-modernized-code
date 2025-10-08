package com.jsf.migration.service;

import com.jsf.migration.exception.FormMigrationException;
import com.jsf.migration.model.Form;
import com.jsf.migration.model.MigrationResult;
import com.jsf.migration.model.MigrationStatus;
import com.jsf.migration.repository.FormRepository;
import com.jsf.migration.repository.MigrationResultRepository;
import com.jsf.migration.util.FormConverter;
import com.jsf.migration.util.MigrationAnalytics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for migrating JSF forms to modern framework components.
 * Handles the conversion process, validation, and tracking of migration results.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormMigrationService {

    private final FormRepository formRepository;
    private final MigrationResultRepository migrationResultRepository;
    private final FormConverter formConverter;
    private final MigrationAnalytics migrationAnalytics;

    /**
     * Migrates a single form by ID
     *
     * @param formId the ID of the form to migrate
     * @return the migration result containing status and details
     * @throws FormMigrationException if the form cannot be found or migration fails
     */
    @Transactional
    public MigrationResult migrateForm(String formId) {
        if (!StringUtils.hasText(formId)) {
            throw new IllegalArgumentException("Form ID cannot be null or empty");
        }

        log.info("Starting migration for form with ID: {}", formId);
        
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormMigrationException("Form not found with ID: " + formId));
        
        return performMigration(form);
    }

    /**
     * Migrates all forms in the system
     *
     * @return a list of migration results
     */
    @Transactional
    public List<MigrationResult> migrateAllForms() {
        log.info("Starting migration for all forms");
        
        List<Form> forms = formRepository.findAll();
        log.info("Found {} forms to migrate", forms.size());
        
        return forms.stream()
                .map(this::performMigration)
                .toList();
    }

    /**
     * Migrates forms by a specific criteria (e.g., by name pattern)
     *
     * @param namePattern the pattern to match form names against
     * @return a list of migration results
     */
    @Transactional
    public List<MigrationResult> migrateFormsByNamePattern(String namePattern) {
        if (!StringUtils.hasText(namePattern)) {
            throw new IllegalArgumentException("Name pattern cannot be null or empty");
        }
        
        log.info("Starting migration for forms matching pattern: {}", namePattern);
        
        List<Form> forms = formRepository.findByNameRegex(namePattern);
        log.info("Found {} forms matching the pattern", forms.size());
        
        return forms.stream()
                .map(this::performMigration)
                .toList();
    }

    /**
     * Gets the migration status for a specific form
     *
     * @param formId the ID of the form
     * @return the migration result if found
     */
    public Optional<MigrationResult> getMigrationStatus(String formId) {
        if (!StringUtils.hasText(formId)) {
            throw new IllegalArgumentException("Form ID cannot be null or empty");
        }
        
        return migrationResultRepository.findByFormId(formId);
    }

    /**
     * Gets all migration results
     *
     * @return a list of all migration results
     */
    public List<MigrationResult> getAllMigrationResults() {
        return migrationResultRepository.findAll();
    }

    /**
     * Gets migration statistics
     *
     * @return a summary of migration statistics
     */
    public MigrationAnalytics.MigrationStatistics getMigrationStatistics() {
        return migrationAnalytics.generateMigrationStatistics();
    }

    /**
     * Performs the actual migration process for a form
     *
     * @param form the form to migrate
     * @return the migration result
     */
    private MigrationResult performMigration(Form form) {
        MigrationResult result = new MigrationResult();
        result.setFormId(form.getFormId());
        result.setFormName(form.getName());
        result.setStartTime(LocalDateTime.now());
        
        try {
            log.debug("Converting form: {}", form.getName());
            
            // Perform the actual conversion
            String convertedFormJson = formConverter.convertToModernFramework(form);
            
            // Validate the conversion result
            boolean isValid = formConverter.validateConvertedForm(convertedFormJson);
            
            if (isValid) {
                result.setStatus(MigrationStatus.SUCCESS);
                result.setConvertedForm(convertedFormJson);
                log.info("Successfully migrated form: {}", form.getName());
            } else {
                result.setStatus(MigrationStatus.PARTIAL);
                result.setConvertedForm(convertedFormJson);
                result.setErrorDetails("Converted form failed validation");
                log.warn("Partially migrated form with validation issues: {}", form.getName());
            }
        } catch (Exception e) {
            log.error("Error migrating form: {}", form.getName(), e);
            result.setStatus(MigrationStatus.FAILED);
            result.setErrorDetails(e.getMessage());
        }
        
        result.setEndTime(LocalDateTime.now());
        
        // Save the migration result
        migrationResultRepository.save(result);
        
        // Update analytics
        migrationAnalytics.recordMigrationResult(result);
        
        return result;
    }

    /**
     * Rollback a migration for a specific form
     *
     * @param formId the ID of the form to rollback
     * @return true if rollback was successful
     */
    @Transactional
    public boolean rollbackMigration(String formId) {
        if (!StringUtils.hasText(formId)) {
            throw new IllegalArgumentException("Form ID cannot be null or empty");
        }
        
        log.info("Rolling back migration for form with ID: {}", formId);
        
        Optional<MigrationResult> migrationResult = migrationResultRepository.findByFormId(formId);
        
        if (migrationResult.isEmpty()) {
            log.warn("No migration found to rollback for form ID: {}", formId);
            return false;
        }
        
        // Delete the migration result
        migrationResultRepository.deleteByFormId(formId);
        
        // Update analytics
        migrationAnalytics.recordRollback(formId);
        
        log.info("Successfully rolled back migration for form ID: {}", formId);
        return true;
    }
}