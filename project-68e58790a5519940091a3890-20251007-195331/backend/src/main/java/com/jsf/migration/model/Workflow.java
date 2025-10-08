package com.jsf.migration.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a workflow that defines the processing steps for form submissions.
 * Workflows contain ordered steps with conditions that determine how form data
 * is processed through the system.
 */
@Document(collection = "workflows")
public class Workflow {

    @Id
    private String workflowId;

    @NotBlank(message = "Workflow name is required")
    @Indexed(unique = true)
    private String name;

    private String description;

    @NotNull(message = "Form ID is required")
    private String formId;

    @NotEmpty(message = "Workflow must contain at least one step")
    private List<WorkflowStep> steps = new ArrayList<>();

    private boolean active = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;
    
    /**
     * Represents a single step in a workflow process
     */
    public static class WorkflowStep {
        @NotNull(message = "Step order is required")
        private Integer order;
        
        @NotBlank(message = "Step type is required")
        private String type;
        
        private WorkflowStepConfig config;
        
        private WorkflowCondition conditions;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public WorkflowStepConfig getConfig() {
            return config;
        }

        public void setConfig(WorkflowStepConfig config) {
            this.config = config;
        }

        public WorkflowCondition getConditions() {
            return conditions;
        }

        public void setConditions(WorkflowCondition conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkflowStep that = (WorkflowStep) o;
            return Objects.equals(order, that.order) &&
                   Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order, type);
        }
    }

    /**
     * Configuration for a workflow step
     */
    public static class WorkflowStepConfig {
        private String action;
        private String destination;
        private String template;
        private List<String> recipients;
        private String serviceEndpoint;
        private String transformationScript;
        private String validationRules;
        
        // Additional configuration properties can be added as needed

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public List<String> getRecipients() {
            return recipients;
        }

        public void setRecipients(List<String> recipients) {
            this.recipients = recipients;
        }

        public String getServiceEndpoint() {
            return serviceEndpoint;
        }

        public void setServiceEndpoint(String serviceEndpoint) {
            this.serviceEndpoint = serviceEndpoint;
        }

        public String getTransformationScript() {
            return transformationScript;
        }

        public void setTransformationScript(String transformationScript) {
            this.transformationScript = transformationScript;
        }

        public String getValidationRules() {
            return validationRules;
        }

        public void setValidationRules(String validationRules) {
            this.validationRules = validationRules;
        }
    }

    /**
     * Conditions that determine whether a workflow step should be executed
     */
    public static class WorkflowCondition {
        private String field;
        private String operator;
        private Object value;
        private List<WorkflowCondition> and;
        private List<WorkflowCondition> or;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public List<WorkflowCondition> getAnd() {
            return and;
        }

        public void setAnd(List<WorkflowCondition> and) {
            this.and = and;
        }

        public List<WorkflowCondition> getOr() {
            return or;
        }

        public void setOr(List<WorkflowCondition> or) {
            this.or = or;
        }
    }

    // Getters and Setters

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Adds a new step to the workflow
     * 
     * @param step The workflow step to add
     * @return The current workflow instance for method chaining
     */
    public Workflow addStep(WorkflowStep step) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }
        this.steps.add(step);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workflow workflow = (Workflow) o;
        return Objects.equals(workflowId, workflow.workflowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workflowId);
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "workflowId='" + workflowId + '\'' +
                ", name='" + name + '\'' +
                ", formId='" + formId + '\'' +
                ", steps=" + steps.size() +
                ", active=" + active +
                '}';
    }
}