package com.ecommerce.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product category in the e-commerce system.
 * Categories can form a hierarchical structure with parent-child relationships.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @NotBlank(message = "Category name is required")
    @Indexed(unique = true)
    private String name;

    @NotBlank(message = "Category slug is required")
    @Indexed(unique = true)
    private String slug;

    private String description;

    @Field("parentId")
    private String parentId;

    @NotNull(message = "Category level is required")
    private Integer level;

    @Builder.Default
    private List<String> path = new ArrayList<>();

    @Builder.Default
    private List<String> attributes = new ArrayList<>();

    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Checks if this category is a root category (has no parent)
     * 
     * @return true if this is a root category, false otherwise
     */
    public boolean isRootCategory() {
        return parentId == null || parentId.isEmpty();
    }

    /**
     * Adds a new attribute to the category's attribute list if it doesn't already exist
     * 
     * @param attribute the attribute to add
     * @return true if the attribute was added, false if it already existed
     */
    public boolean addAttribute(String attribute) {
        if (attribute != null && !attribute.isEmpty() && !attributes.contains(attribute)) {
            return attributes.add(attribute);
        }
        return false;
    }

    /**
     * Removes an attribute from the category's attribute list
     * 
     * @param attribute the attribute to remove
     * @return true if the attribute was removed, false if it wasn't in the list
     */
    public boolean removeAttribute(String attribute) {
        return attributes.remove(attribute);
    }

    /**
     * Updates the path of this category based on its parent's path
     * 
     * @param parentPath the path of the parent category
     */
    public void updatePath(List<String> parentPath) {
        path = new ArrayList<>(parentPath);
        path.add(slug);
    }
}