package com.jsf.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a JSF form definition in the migration system.
 * This model maps to the Forms collection in MongoDB.
 */
@Document(collection = "Forms")
public class Form {

    @Id
    private String formId;

    @NotBlank(message = "Form name is required")
    @Size(max = 100, message = "Form name cannot exceed 100 characters")
    @Indexed(unique = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotEmpty(message = "Form must contain at least one field")
    private List<FormField> fields = new ArrayList<>();

    private Map<String, Object> layout = new HashMap<>();

    private boolean active = true;

    private String createdBy;

    private LocalDateTime createdAt;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedAt;

    private String version;

    /**
     * Represents a field within a form.
     */
    public static class FormField {
        @NotBlank(message = "Field name is required")
        private String name;

        @NotBlank(message = "Field type is required")
        private String type;

        @NotBlank(message = "Field label is required")
        private String label;

        private boolean required;

        private Map<String, Object> validation = new HashMap<>();

        private Map<String, Object> properties = new HashMap<>();

        public FormField() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Map<String, Object> getValidation() {
            return validation;
        }

        public void setValidation(Map<String, Object> validation) {
            this.validation = validation;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FormField formField = (FormField) o;
            return required == formField.required &&
                   Objects.equals(name, formField.name) &&
                   Objects.equals(type, formField.type) &&
                   Objects.equals(label, formField.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, label, required);
        }
    }

    public Form() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * Creates a new form with the specified name.
     *
     * @param name The name of the form
     */
    public Form(String name) {
        this();
        this.name = name;
    }

    /**
     * Creates a new form with the specified name and description.
     *
     * @param name The name of the form
     * @param description The description of the form
     */
    public Form(String name, String description) {
        this(name);
        this.description = description;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
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

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    /**
     * Adds a field to the form.
     *
     * @param field The field to add
     * @return This form instance for method chaining
     */
    public Form addField(FormField field) {
        this.fields.add(field);
        return this;
    }

    public Map<String, Object> getLayout() {
        return layout;
    }

    public void setLayout(Map<String, Object> layout) {
        this.layout = layout;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Updates the last modified timestamp to the current time.
     */
    public void updateLastModifiedAt() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Form form = (Form) o;
        return Objects.equals(formId, form.formId) ||
               (Objects.equals(name, form.name) && Objects.equals(version, form.version));
    }

    @Override
    public int hashCode() {
        return Objects.hash(formId, name, version);
    }

    @Override
    public String toString() {
        return "Form{" +
                "formId='" + formId + '\'' +
                ", name='" + name + '\'' +
                ", fields=" + fields.size() +
                ", active=" + active +
                ", version='" + version + '\'' +
                '}';
    }
}