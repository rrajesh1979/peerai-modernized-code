package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization entity operations.
 * Provides methods to interact with the Organizations collection in MongoDB.
 */
@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

    /**
     * Find an organization by its name
     * 
     * @param name the organization name to search for
     * @return an Optional containing the organization if found
     */
    Optional<Organization> findByName(String name);
    
    /**
     * Find organizations containing the given name pattern (case-insensitive)
     * 
     * @param namePattern the pattern to match against organization names
     * @return a list of matching organizations
     */
    List<Organization> findByNameContainingIgnoreCase(String namePattern);
    
    /**
     * Check if an organization with the given name exists
     * 
     * @param name the organization name to check
     * @return true if an organization with the name exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Find organizations by city
     * 
     * @param city the city to search for
     * @return a list of organizations in the specified city
     */
    @Query("{'address.city': ?0}")
    List<Organization> findByCity(String city);
    
    /**
     * Find organizations by country
     * 
     * @param country the country to search for
     * @return a list of organizations in the specified country
     */
    @Query("{'address.country': ?0}")
    List<Organization> findByCountry(String country);
    
    /**
     * Find organizations with a specific industry
     * 
     * @param industry the industry to search for
     * @return a list of organizations in the specified industry
     */
    List<Organization> findByIndustry(String industry);
    
    /**
     * Find organizations founded after the given year
     * 
     * @param year the minimum founding year
     * @return a list of organizations founded after the specified year
     */
    @Query("{'foundedYear': {$gte: ?0}}")
    List<Organization> findByFoundedYearGreaterThanEqual(int year);
    
    /**
     * Find organizations with employee count greater than or equal to the specified value
     * 
     * @param count the minimum employee count
     * @return a list of organizations with at least the specified number of employees
     */
    @Query("{'employeeCount': {$gte: ?0}}")
    List<Organization> findByEmployeeCountGreaterThanEqual(int count);
    
    /**
     * Delete an organization by its name
     * 
     * @param name the name of the organization to delete
     * @return the number of organizations deleted
     */
    long deleteByName(String name);
}