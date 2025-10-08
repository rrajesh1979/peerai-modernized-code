package com.ecommerce.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product entity representing items in the product catalog.
 * Maps to the 'products' collection in MongoDB.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    @NotBlank(message = "SKU is required")
    @Indexed(unique = true)
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Builder.Default
    private String currency = "USD";

    @NotBlank(message = "Category is required")
    private String category;

    private String subcategory;

    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> dimensions = new HashMap<>();

    @Builder.Default
    private Double weight = 0.0;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isFeatured = false;

    @Builder.Default
    private Integer stockLevel = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("metadata")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Checks if the product is in stock.
     *
     * @return true if the product has stock available, false otherwise
     */
    public boolean isInStock() {
        return stockLevel != null && stockLevel > 0;
    }

    /**
     * Adds a product attribute.
     *
     * @param key attribute name
     * @param value attribute value
     * @return the current product instance for method chaining
     */
    public Product addAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
        return this;
    }

    /**
     * Adds a tag to the product.
     *
     * @param tag the tag to add
     * @return the current product instance for method chaining
     */
    public Product addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
        return this;
    }

    /**
     * Sets a dimension value for the product.
     *
     * @param dimensionName the dimension name (e.g., "length", "width", "height")
     * @param value the dimension value
     * @return the current product instance for method chaining
     */
    public Product setDimension(String dimensionName, Object value) {
        if (dimensions == null) {
            dimensions = new HashMap<>();
        }
        dimensions.put(dimensionName, value);
        return this;
    }
}