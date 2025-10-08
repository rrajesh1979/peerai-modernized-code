package com.jsf.migration.service;

import com.jsf.migration.exception.ResourceNotFoundException;
import com.jsf.migration.exception.WorkflowException;
import com.jsf.migration.model.FormSubmission;
import com.jsf.migration.model.Workflow;
import com.jsf.migration.model.WorkflowExecution;
import com.jsf.migration.model.WorkflowStep;
import com.jsf.migration.repository.FormSubmissionRepository;
import com.jsf.migration.repository.WorkflowExecutionRepository;
import com.jsf.migration.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing workflow operations including creation, execution, and monitoring.
 * Handles the business logic for workflow processing and integration with form submissions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final FormSubmissionRepository formSubmissionRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final Map<String, WorkflowStepHandler> workflowStepHandlers;

    /**
     * Retrieves all workflows with pagination support
     *
     * @param pageable Pagination information
     * @return Page of workflows
     */
    public Page<Workflow> getAllWorkflows(Pageable pageable) {
        log.debug("Retrieving all workflows with pagination: {}", pageable);
        return workflowRepository.findAll(pageable);
    }

    /**
     * Retrieves workflows associated with a specific form
     *
     * @param formId The ID of the form
     * @return List of workflows for the form
     */
    public List<Workflow> getWorkflowsByFormId(String formId) {
        log.debug("Retrieving workflows for form ID: {}", formId);
        return workflowRepository.findByFormId(formId);
    }

    /**
     * Retrieves a workflow by its ID
     *
     * @param workflowId The ID of the workflow to retrieve
     * @return The workflow
     * @throws ResourceNotFoundException if the workflow is not found
     */
    public Workflow getWorkflowById(String workflowId) {
        log.debug("Retrieving workflow with ID: {}", workflowId);
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with ID: " + workflowId));
    }

    /**
     * Creates a new workflow
     *
     * @param workflow The workflow to create
     * @return The created workflow
     */
    @Transactional
    public Workflow createWorkflow(Workflow workflow) {
        log.info("Creating new workflow: {}", workflow.getName());
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());
        validateWorkflow(workflow);
        return workflowRepository.save(workflow);
    }

    /**
     * Updates an existing workflow
     *
     * @param workflowId The ID of the workflow to update
     * @param workflow   The updated workflow data
     * @return The updated workflow
     * @throws ResourceNotFoundException if the workflow is not found
     */
    @Transactional
    public Workflow updateWorkflow(String workflowId, Workflow workflow) {
        log.info("Updating workflow with ID: {}", workflowId);
        Workflow existingWorkflow = getWorkflowById(workflowId);
        
        existingWorkflow.setName(workflow.getName());
        existingWorkflow.setDescription(workflow.getDescription());
        existingWorkflow.setFormId(workflow.getFormId());
        existingWorkflow.setSteps(workflow.getSteps());
        existingWorkflow.setActive(workflow.isActive());
        existingWorkflow.setUpdatedAt(LocalDateTime.now());
        
        validateWorkflow(existingWorkflow);
        return workflowRepository.save(existingWorkflow);
    }

    /**
     * Deletes a workflow by its ID
     *
     * @param workflowId The ID of the workflow to delete
     * @throws ResourceNotFoundException if the workflow is not found
     */
    @Transactional
    public void deleteWorkflow(String workflowId) {
        log.info("Deleting workflow with ID: {}", workflowId);
        Workflow workflow = getWorkflowById(workflowId);
        workflowRepository.delete(workflow);
    }

    /**
     * Activates or deactivates a workflow
     *
     * @param workflowId The ID of the workflow
     * @param active     The activation status to set
     * @return The updated workflow
     */
    @Transactional
    public Workflow setWorkflowActive(String workflowId, boolean active) {
        log.info("Setting workflow {} active status to: {}", workflowId, active);
        Workflow workflow = getWorkflowById(workflowId);
        workflow.setActive(active);
        workflow.setUpdatedAt(LocalDateTime.now());
        return workflowRepository.save(workflow);
    }

    /**
     * Executes a workflow for a form submission
     *
     * @param submissionId The ID of the form submission
     * @return The workflow execution result
     * @throws ResourceNotFoundException if the submission or workflow is not found
     * @throws WorkflowException         if there's an error during workflow execution
     */
    @Transactional
    public WorkflowExecution executeWorkflow(String submissionId) {
        log.info("Executing workflow for submission ID: {}", submissionId);
        
        FormSubmission submission = formSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Form submission not found with ID: " + submissionId));
        
        List<Workflow> workflows = workflowRepository.findByFormIdAndActive(submission.getFormId(), true);
        if (workflows.isEmpty()) {
            log.warn("No active workflow found for form ID: {}", submission.getFormId());
            throw new WorkflowException("No active workflow found for this form submission");
        }
        
        // Use the first active workflow (in a real system, you might have more complex selection logic)
        Workflow workflow = workflows.get(0);
        
        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflowId(workflow.getId());
        execution.setSubmissionId(submissionId);
        execution.setUserId(submission.getUserId());
        execution.setStartTime(LocalDateTime.now());
        execution.setStatus("RUNNING");
        execution.setStepResults(new HashMap<>());
        
        workflowExecutionRepository.save(execution);
        
        try {
            processWorkflowSteps(workflow, submission, execution);
            execution.setStatus("COMPLETED");
            execution.setEndTime(LocalDateTime.now());
            log.info("Workflow execution completed successfully for submission ID: {}", submissionId);
        } catch (Exception e) {
            execution.setStatus("FAILED");
            execution.setEndTime(LocalDateTime.now());
            execution.setErrorMessage(e.getMessage());
            log.error("Workflow execution failed for submission ID: {}", submissionId, e);
        }
        
        return workflowExecutionRepository.save(execution);
    }

    /**
     * Retrieves workflow execution history for a specific form submission
     *
     * @param submissionId The ID of the form submission
     * @return List of workflow executions
     */
    public List<WorkflowExecution> getWorkflowExecutionHistory(String submissionId) {
        log.debug("Retrieving workflow execution history for submission ID: {}", submissionId);
        return workflowExecutionRepository.findBySubmissionIdOrderByStartTimeDesc(submissionId);
    }

    /**
     * Retrieves a specific workflow execution by its ID
     *
     * @param executionId The ID of the workflow execution
     * @return The workflow execution
     * @throws ResourceNotFoundException if the execution is not found
     */
    public WorkflowExecution getWorkflowExecution(String executionId) {
        log.debug("Retrieving workflow execution with ID: {}", executionId);
        return workflowExecutionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow execution not found with ID: " + executionId));
    }

    /**
     * Validates a workflow configuration
     *
     * @param workflow The workflow to validate
     * @throws WorkflowException if the workflow configuration is invalid
     */
    private void validateWorkflow(Workflow workflow) {
        if (workflow.getName() == null || workflow.getName().trim().isEmpty()) {
            throw new WorkflowException("Workflow name cannot be empty");
        }
        
        if (workflow.getFormId() == null || workflow.getFormId().trim().isEmpty()) {
            throw new WorkflowException("Workflow must be associated with a form");
        }
        
        if (workflow.getSteps() == null || workflow.getSteps().isEmpty()) {
            throw new WorkflowException("Workflow must contain at least one step");
        }
        
        // Validate step order and configuration
        for (int i = 0; i < workflow.getSteps().size(); i++) {
            WorkflowStep step = workflow.getSteps().get(i);
            
            if (step.getOrder() != i + 1) {
                throw new WorkflowException("Workflow steps must be in sequential order");
            }
            
            if (step.getType() == null || step.getType().trim().isEmpty()) {
                throw new WorkflowException("Step type cannot be empty");
            }
            
            // Check if we have a handler for this step type
            if (!workflowStepHandlers.containsKey(step.getType())) {
                throw new WorkflowException("Unsupported workflow step type: " + step.getType());
            }
            
            // Additional validation could be performed by the specific step handler
            WorkflowStepHandler handler = workflowStepHandlers.get(step.getType());
            handler.validateStepConfiguration(step.getConfig());
        }
    }

    /**
     * Processes all steps in a workflow
     *
     * @param workflow   The workflow to process
     * @param submission The form submission
     * @param execution  The workflow execution record
     * @throws WorkflowException if there's an error during processing
     */
    private void processWorkflowSteps(Workflow workflow, FormSubmission submission, WorkflowExecution execution) {
        Map<String, Object> context = new HashMap<>();
        context.put("submission", submission);
        
        for (WorkflowStep step : workflow.getSteps()) {
            log.debug("Processing workflow step: {} (type: {})", step.getOrder(), step.getType());
            
            // Check if step should be executed based on conditions
            if (!evaluateStepConditions(step, context)) {
                log.debug("Skipping step {} due to conditions not met", step.getOrder());
                execution.getStepResults().put(String.valueOf(step.getOrder()), Map.of("status", "SKIPPED"));
                continue;
            }
            
            try {
                // Get the appropriate handler for this step type
                WorkflowStepHandler handler = Optional.ofNullable(workflowStepHandlers.get(step.getType()))
                        .orElseThrow(() -> new WorkflowException("No handler found for step type: " + step.getType()));
                
                // Execute the step
                Map<String, Object> result = handler.executeStep(step.getConfig(), context);
                
                // Store the result
                execution.getStepResults().put(String.valueOf(step.getOrder()), result);
                
                // Update the context with the result for subsequent steps
                context.put("step" + step.getOrder() + "Result", result);
                
            } catch (Exception e) {
                log.error("Error executing workflow step {}: {}", step.getOrder(), e.getMessage(), e);
                execution.getStepResults().put(String.valueOf(step.getOrder()), 
                        Map.of("status", "ERROR", "message", e.getMessage()));
                throw new WorkflowException("Error in workflow step " + step.getOrder() + ": " + e.getMessage(), e);
            }
        }
        
        // Update the submission status after workflow completion
        submission.setStatus("PROCESSED");
        submission.setProcessedAt(LocalDateTime.now());
        formSubmissionRepository.save(submission);
    }

    /**
     * Evaluates whether a workflow step should be executed based on its conditions
     *
     * @param step    The workflow step
     * @param context The execution context
     * @return true if the step should be executed, false otherwise
     */
    private boolean evaluateStepConditions(WorkflowStep step, Map<String, Object> context) {
        // If no conditions are specified, always execute the step
        if (step.getConditions() == null || step.getConditions().isEmpty()) {
            return true;
        }
        
        // Simple condition evaluation logic - could be expanded to a more sophisticated rules engine
        try {
            // Example: Check for a field value condition
            if (step.getConditions().containsKey("fieldEquals")) {
                Map<String, String> fieldEquals = (Map<String, String>) step.getConditions().get("fieldEquals");
                FormSubmission submission = (FormSubmission) context.get("submission");
                
                for (Map.Entry<String, String> entry : fieldEquals.entrySet()) {
                    String fieldName = entry.getKey();
                    String expectedValue = entry.getValue();
                    
                    Object actualValue = submission.getData().get(fieldName);
                    if (actualValue == null || !actualValue.toString().equals(expectedValue)) {
                        return false;
                    }
                }
            }
            
            // Example: Check for a previous step result condition
            if (step.getConditions().containsKey("previousStepStatus")) {
                String requiredStatus = (String) step.getConditions().get("previousStepStatus");
                int previousStepOrder = step.getOrder() - 1;
                
                Map<String, Object> previousStepResult = 
                        (Map<String, Object>) context.get("step" + previousStepOrder + "Result");
                
                if (previousStepResult == null || !requiredStatus.equals(previousStepResult.get("status"))) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.warn("Error evaluating step conditions: {}", e.getMessage());
            // In case of errors in condition evaluation, default to executing the step
            return true;
        }
    }
}

/**
 * Interface for workflow step handlers
 * Each step type should have its own implementation
 */
interface WorkflowStepHandler {
    /**
     * Validates the configuration for a workflow step
     *
     * @param config The step configuration
     * @throws WorkflowException if the configuration is invalid
     */
    void validateStepConfiguration(Map<String, Object> config);
    
    /**
     * Executes a workflow step
     *
     * @param config  The step configuration
     * @param context The execution context
     * @return The execution result
     * @throws WorkflowException if there's an error during execution
     */
    Map<String, Object> executeStep(Map<String, Object> config, Map<String, Object> context);
}