package com.jsf.migration.controller;

import com.jsf.migration.dto.FormDTO;
import com.jsf.migration.dto.FormSubmissionDTO;
import com.jsf.migration.exception.ResourceNotFoundException;
import com.jsf.migration.model.Form;
import com.jsf.migration.model.FormSubmission;
import com.jsf.migration.service.FormService;
import com.jsf.migration.service.FormSubmissionService;
import com.jsf.migration.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Form resources and form submissions.
 * Provides endpoints for CRUD operations on forms and handling form submissions.
 */
@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
@Slf4j
public class FormController {

    private final FormService formService;
    private final FormSubmissionService formSubmissionService;
    private final WorkflowService workflowService;

    /**
     * Creates a new form.
     *
     * @param formDTO the form to create
     * @return the created form
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormDTO> createForm(@Valid @RequestBody FormDTO formDTO) {
        log.info("REST request to create Form : {}", formDTO);
        FormDTO result = formService.createForm(formDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing form.
     *
     * @param id the ID of the form to update
     * @param formDTO the form with updated fields
     * @return the updated form
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormDTO> updateForm(
            @PathVariable String id,
            @Valid @RequestBody FormDTO formDTO) {
        log.info("REST request to update Form : {}, {}", id, formDTO);
        
        if (!id.equals(formDTO.getId())) {
            throw new IllegalArgumentException("IDs don't match");
        }
        
        FormDTO result = formService.updateForm(formDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets all forms.
     *
     * @return the list of forms
     */
    @GetMapping
    public ResponseEntity<List<FormDTO>> getAllForms() {
        log.info("REST request to get all Forms");
        List<FormDTO> forms = formService.getAllForms();
        return ResponseEntity.ok(forms);
    }

    /**
     * Gets a form by ID.
     *
     * @param id the ID of the form to retrieve
     * @return the form
     */
    @GetMapping("/{id}")
    public ResponseEntity<FormDTO> getForm(@PathVariable String id) {
        log.info("REST request to get Form : {}", id);
        FormDTO formDTO = formService.getFormById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + id));
        return ResponseEntity.ok(formDTO);
    }

    /**
     * Deletes a form by ID.
     *
     * @param id the ID of the form to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteForm(@PathVariable String id) {
        log.info("REST request to delete Form : {}", id);
        formService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Submits a form with the provided data.
     *
     * @param id the ID of the form to submit
     * @param formData the form data
     * @param authentication the current user authentication
     * @return the created form submission
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<FormSubmissionDTO> submitForm(
            @PathVariable String id,
            @Valid @RequestBody Map<String, Object> formData,
            Authentication authentication) {
        log.info("REST request to submit Form : {}", id);
        
        String userId = authentication.getName();
        FormSubmissionDTO submission = formSubmissionService.createSubmission(id, userId, formData);
        
        // Trigger workflow if exists
        workflowService.processSubmission(submission.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    /**
     * Gets all submissions for a specific form.
     *
     * @param id the ID of the form
     * @return the list of form submissions
     */
    @GetMapping("/{id}/submissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FormSubmissionDTO>> getFormSubmissions(@PathVariable String id) {
        log.info("REST request to get all submissions for Form : {}", id);
        List<FormSubmissionDTO> submissions = formSubmissionService.getSubmissionsByFormId(id);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Gets a specific submission for a form.
     *
     * @param formId the ID of the form
     * @param submissionId the ID of the submission
     * @return the form submission
     */
    @GetMapping("/{formId}/submissions/{submissionId}")
    public ResponseEntity<FormSubmissionDTO> getFormSubmission(
            @PathVariable String formId,
            @PathVariable String submissionId,
            Authentication authentication) {
        log.info("REST request to get submission {} for Form : {}", submissionId, formId);
        
        FormSubmissionDTO submission = formSubmissionService.getSubmissionById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        
        // Verify the submission belongs to the specified form
        if (!submission.getFormId().equals(formId)) {
            throw new ResourceNotFoundException("Submission not found for the specified form");
        }
        
        // Check if user is admin or the submission owner
        String userId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
        if (!isAdmin && !submission.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(submission);
    }

    /**
     * Gets the form layout configuration.
     *
     * @param id the ID of the form
     * @return the form layout configuration
     */
    @GetMapping("/{id}/layout")
    public ResponseEntity<Object> getFormLayout(@PathVariable String id) {
        log.info("REST request to get layout for Form : {}", id);
        Form form = formService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + id));
        return ResponseEntity.ok(form.getLayout());
    }

    /**
     * Updates the form layout configuration.
     *
     * @param id the ID of the form
     * @param layout the new layout configuration
     * @return the updated form
     */
    @PutMapping("/{id}/layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormDTO> updateFormLayout(
            @PathVariable String id,
            @RequestBody Object layout) {
        log.info("REST request to update layout for Form : {}", id);
        FormDTO updatedForm = formService.updateFormLayout(id, layout);
        return ResponseEntity.ok(updatedForm);
    }
}