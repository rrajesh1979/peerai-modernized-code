package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations
 * Provides methods to interact with the Products collection in MongoDB
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /**
     * Find a product by its SKU
     * @param sku the product SKU
     * @return an Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Find all products belonging to a specific category
     * @param category the category name
     * @return list of products in the category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find all products belonging to a specific category and subcategory
     * @param category the category name
     * @param subcategory the subcategory name
     * @return list of products in the category and subcategory
     */
    List<Product> findByCategoryAndSubcategory(String category, String subcategory);
    
    /**
     * Find products with price less than or equal to the specified amount
     * @param price the maximum price
     * @param pageable pagination information
     * @return a page of products
     */
    Page<Product> findByPriceLessThanEqual(BigDecimal price, Pageable pageable);
    
    /**
     * Find products with price greater than or equal to the specified amount
     * @param price the minimum price
     * @param pageable pagination information
     * @return a page of products
     */
    Page<Product> findByPriceGreaterThanEqual(BigDecimal price, Pageable pageable);
    
    /**
     * Find products with price between the specified range
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a page of products
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Search products by name containing the search term (case insensitive)
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of products matching the search term
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<Product> searchByNameContainingIgnoreCase(String searchTerm, Pageable pageable);
    
    /**
     * Search products by description containing the search term (case insensitive)
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of products matching the search term
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    Page<Product> searchByDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);
    
    /**
     * Find products by specific attribute value
     * @param attributeName the name of the attribute
     * @param attributeValue the value of the attribute
     * @return list of products with the specified attribute value
     */
    @Query("{'attributes.?0': ?1}")
    List<Product> findByAttributeValue(String attributeName, String attributeValue);
    
    /**
     * Find products that are in stock (available quantity > 0)
     * @param pageable pagination information
     * @return a page of in-stock products
     */
    @Query("{'inventory.available': {$gt: 0}}")
    Page<Product> findInStockProducts(Pageable pageable);
    
    /**
     * Find products that are low in stock (available quantity <= reorderLevel)
     * @return list of products that need restocking
     */
    @Query("{'inventory.available': {$lte: '$inventory.reorderLevel'}}")
    List<Product> findLowStockProducts();
    
    /**
     * Find products by tags
     * @param tag the tag to search for
     * @return list of products with the specified tag
     */
    List<Product> findByTagsContaining(String tag);
    
    /**
     * Find featured products
     * @param pageable pagination information
     * @return a page of featured products
     */
    Page<Product> findByFeaturedTrue(Pageable pageable);
    
    /**
     * Find active products
     * @param pageable pagination information
     * @return a page of active products
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Count products by category
     * @param category the category name
     * @return the count of products in the category
     */
    long countByCategory(String category);
}