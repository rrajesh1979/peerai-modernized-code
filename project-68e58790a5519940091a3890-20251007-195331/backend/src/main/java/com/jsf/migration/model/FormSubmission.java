package com.jsf.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a user's form submission in the system.
 * This entity stores all data submitted by users through forms along with metadata
 * about the submission process.
 */
@Document(collection = "FormSubmissions")
public class FormSubmission {

    @Id
    private String submissionId;

    @Indexed
    @Field("formId")
    private String formId;

    @Indexed
    @Field("userId")
    private String userId;

    @Field("data")
    private Map<String, Object> data;

    @Field("status")
    private SubmissionStatus status;

    @Field("submittedAt")
    private LocalDateTime submittedAt;

    @Field("processedAt")
    private LocalDateTime processedAt;

    /**
     * Enum representing the possible states of a form submission
     */
    public enum SubmissionStatus {
        DRAFT,
        SUBMITTED,
        PROCESSING,
        COMPLETED,
        REJECTED,
        ERROR
    }

    /**
     * Default constructor for Spring Data
     */
    public FormSubmission() {
    }

    /**
     * Constructor with required fields
     *
     * @param formId the ID of the form that was submitted
     * @param userId the ID of the user who submitted the form
     * @param data the form submission data
     */
    public FormSubmission(String formId, String userId, Map<String, Object> data) {
        this.formId = formId;
        this.userId = userId;
        this.data = data;
        this.status = SubmissionStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    /**
     * Updates the status to COMPLETED and sets the processedAt timestamp
     */
    public void markAsCompleted() {
        this.status = SubmissionStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Updates the status to REJECTED and sets the processedAt timestamp
     */
    public void markAsRejected() {
        this.status = SubmissionStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Updates the status to ERROR and sets the processedAt timestamp
     */
    public void markAsError() {
        this.status = SubmissionStatus.ERROR;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Updates the status to PROCESSING
     */
    public void markAsProcessing() {
        this.status = SubmissionStatus.PROCESSING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormSubmission that = (FormSubmission) o;
        return Objects.equals(submissionId, that.submissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submissionId);
    }

    @Override
    public String toString() {
        return "FormSubmission{" +
                "submissionId='" + submissionId + '\'' +
                ", formId='" + formId + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", submittedAt=" + submittedAt +
                ", processedAt=" + processedAt +
                '}';
    }
}