package com.ecommerce.service;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing product-related operations.
 * Handles CRUD operations and business logic for products.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final AuditService auditService;

    /**
     * Retrieves all products with pagination support
     *
     * @param pageable Pagination information
     * @return Page of products
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        log.debug("Fetching products page: {}", pageable);
        return productRepository.findAll(pageable);
    }

    /**
     * Retrieves a product by its ID
     *
     * @param id Product ID
     * @return Product if found
     * @throws ProductNotFoundException if product not found
     */
    public Product getProductById(String id) {
        log.debug("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    /**
     * Retrieves a product by its SKU
     *
     * @param sku Product SKU
     * @return Product if found
     * @throws ProductNotFoundException if product not found
     */
    public Product getProductBySku(String sku) {
        log.debug("Fetching product with SKU: {}", sku);
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
    }

    /**
     * Creates a new product
     *
     * @param product Product to create
     * @return Created product
     */
    @Transactional
    public Product createProduct(Product product) {
        log.debug("Creating new product: {}", product.getName());
        
        // Validate category exists
        if (product.getCategory() != null) {
            categoryRepository.findByName(product.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + product.getCategory()));
        }
        
        // Set creation timestamp
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        
        // Log audit event
        auditService.logEvent("PRODUCT_CREATED", "Product", savedProduct.getId(), null, savedProduct);
        
        return savedProduct;
    }

    /**
     * Updates an existing product
     *
     * @param id Product ID
     * @param productDetails Updated product details
     * @return Updated product
     * @throws ProductNotFoundException if product not found
     */
    @Transactional
    public Product updateProduct(String id, Product productDetails) {
        log.debug("Updating product with ID: {}", id);
        
        Product existingProduct = getProductById(id);
        
        // Store original for audit
        Product originalProduct = new Product();
        // Copy all fields from existing product to original
        copyProductFields(existingProduct, originalProduct);
        
        // Update fields
        if (StringUtils.hasText(productDetails.getName())) {
            existingProduct.setName(productDetails.getName());
        }
        
        if (StringUtils.hasText(productDetails.getDescription())) {
            existingProduct.setDescription(productDetails.getDescription());
        }
        
        if (productDetails.getPrice() != null) {
            existingProduct.setPrice(productDetails.getPrice());
        }
        
        if (StringUtils.hasText(productDetails.getCurrency())) {
            existingProduct.setCurrency(productDetails.getCurrency());
        }
        
        if (StringUtils.hasText(productDetails.getCategory())) {
            // Validate category exists
            categoryRepository.findByName(productDetails.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + productDetails.getCategory()));
            existingProduct.setCategory(productDetails.getCategory());
        }
        
        if (StringUtils.hasText(productDetails.getSubcategory())) {
            existingProduct.setSubcategory(productDetails.getSubcategory());
        }
        
        if (productDetails.getAttributes() != null) {
            existingProduct.setAttributes(productDetails.getAttributes());
        }
        
        if (productDetails.getTags() != null) {
            existingProduct.setTags(productDetails.getTags());
        }
        
        if (productDetails.getImages() != null) {
            existingProduct.setImages(productDetails.getImages());
        }
        
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Log audit event
        auditService.logEvent("PRODUCT_UPDATED", "Product", updatedProduct.getId(), originalProduct, updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Deletes a product by ID
     *
     * @param id Product ID
     * @throws ProductNotFoundException if product not found
     */
    @Transactional
    public void deleteProduct(String id) {
        log.debug("Deleting product with ID: {}", id);
        
        Product product = getProductById(id);
        
        // Check if product has inventory
        boolean hasInventory = inventoryRepository.existsByProductId(id);
        if (hasInventory) {
            log.warn("Cannot delete product with ID: {} as it has inventory records", id);
            throw new IllegalStateException("Cannot delete product with existing inventory records");
        }
        
        productRepository.delete(product);
        
        // Log audit event
        auditService.logEvent("PRODUCT_DELETED", "Product", id, product, null);
    }

    /**
     * Searches for products based on various criteria
     *
     * @param query Search query for name or description
     * @param category Category filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param pageable Pagination information
     * @return Page of matching products
     */
    public Page<Product> searchProducts(String query, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice, 
                                        Pageable pageable) {
        log.debug("Searching products with query: {}, category: {}, price range: {}-{}", 
                query, category, minPrice, maxPrice);
        
        return productRepository.findBySearchCriteria(query, category, minPrice, maxPrice, pageable);
    }

    /**
     * Retrieves products by category
     *
     * @param category Category name
     * @param pageable Pagination information
     * @return Page of products in the specified category
     */
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        log.debug("Fetching products by category: {}", category);
        
        // Validate category exists
        categoryRepository.findByName(category)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + category));
        
        return productRepository.findByCategory(category, pageable);
    }

    /**
     * Updates product attributes
     *
     * @param id Product ID
     * @param attributes Map of attributes to update
     * @return Updated product
     * @throws ProductNotFoundException if product not found
     */
    @Transactional
    public Product updateProductAttributes(String id, Map<String, String> attributes) {
        log.debug("Updating attributes for product with ID: {}", id);
        
        Product product = getProductById(id);
        Product originalProduct = new Product();
        copyProductFields(product, originalProduct);
        
        // Update or add attributes
        if (product.getAttributes() == null) {
            product.setAttributes(attributes);
        } else {
            product.getAttributes().putAll(attributes);
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        
        // Log audit event
        auditService.logEvent("PRODUCT_ATTRIBUTES_UPDATED", "Product", id, originalProduct, updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Updates product price
     *
     * @param id Product ID
     * @param price New price
     * @return Updated product
     * @throws ProductNotFoundException if product not found
     */
    @Transactional
    public Product updateProductPrice(String id, BigDecimal price) {
        log.debug("Updating price for product with ID: {} to {}", id, price);
        
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be a non-negative value");
        }
        
        Product product = getProductById(id);
        Product originalProduct = new Product();
        copyProductFields(product, originalProduct);
        
        product.setPrice(price);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        
        // Log audit event
        auditService.logEvent("PRODUCT_PRICE_UPDATED", "Product", id, originalProduct, updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Retrieves products by tags
     *
     * @param tags List of tags to search for
     * @param pageable Pagination information
     * @return Page of products with matching tags
     */
    public Page<Product> getProductsByTags(List<String> tags, Pageable pageable) {
        log.debug("Fetching products by tags: {}", tags);
        return productRepository.findByTagsIn(tags, pageable);
    }

    /**
     * Checks if a product with the given SKU exists
     *
     * @param sku SKU to check
     * @return true if product exists, false otherwise
     */
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    /**
     * Helper method to copy product fields for audit purposes
     *
     * @param source Source product
     * @param target Target product
     */
    private void copyProductFields(Product source, Product target) {
        target.setId(source.getId());
        target.setSku(source.getSku());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPrice(source.getPrice());
        target.setCurrency(source.getCurrency());
        target.setCategory(source.getCategory());
        target.setSubcategory(source.getSubcategory());
        target.setAttributes(source.getAttributes());
        target.setTags(source.getTags());
        target.setImages(source.getImages());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setIsActive(source.getIsActive());
    }
}