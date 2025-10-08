package com.ecommerce.controller;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductSearchCriteria;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product API", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Returns a paginated list of products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("REST request to get a page of Products");
        Page<ProductDTO> page = productService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable String id) {
        log.debug("REST request to get Product : {}", id);
        ProductDTO productDTO = productService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Create a new product", description = "Creates a new product and returns the created product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Product to create") @Valid @RequestBody ProductDTO productDTO) {
        log.debug("REST request to save Product : {}", productDTO);
        ProductDTO result = productService.save(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update an existing product", description = "Updates a product and returns the updated product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Updated product") @Valid @RequestBody ProductDTO productDTO) {
        log.debug("REST request to update Product : {}, {}", id, productDTO);
        
        if (!productService.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        productDTO.setId(id);
        ProductDTO result = productService.update(productDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        log.debug("REST request to delete Product : {}", id);
        
        if (!productService.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products", description = "Returns products matching the search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @PostMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @Parameter(description = "Search criteria") @RequestBody ProductSearchCriteria criteria,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("REST request to search Products with criteria: {}", criteria);
        Page<ProductDTO> page = productService.search(criteria, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get products by category", description = "Returns products belonging to a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("REST request to get Products by category : {}", categoryId);
        Page<ProductDTO> page = productService.findByCategory(categoryId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Update product inventory", description = "Updates the inventory for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<Void> updateProductInventory(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Parameter(description = "Quantity to add (positive) or subtract (negative)") @RequestParam int quantityChange,
            @Parameter(description = "Warehouse ID") @RequestParam String warehouseId) {
        log.debug("REST request to update Product inventory : {}, change: {}, warehouse: {}", 
                id, quantityChange, warehouseId);
        
        productService.updateInventory(id, warehouseId, quantityChange);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get featured products", description = "Returns a list of featured products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved featured products")
    })
    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts(
            @Parameter(description = "Maximum number of products to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("REST request to get featured Products, limit: {}", limit);
        List<ProductDTO> featuredProducts = productService.findFeaturedProducts(limit);
        return ResponseEntity.ok(featuredProducts);
    }
}