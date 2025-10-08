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
     * @param namePattern the pattern to search for in organization names
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
     * Find organizations by address city
     * 
     * @param city the city to search for
     * @return a list of organizations in the specified city
     */
    List<Organization> findByAddressCity(String city);
    
    /**
     * Find organizations by address country
     * 
     * @param country the country to search for
     * @return a list of organizations in the specified country
     */
    List<Organization> findByAddressCountry(String country);
    
    /**
     * Custom query to find organizations with active status
     * 
     * @return a list of active organizations
     */
    @Query("{'status': 'ACTIVE'}")
    List<Organization> findActiveOrganizations();
    
    /**
     * Custom query to find organizations created after a specific date
     * 
     * @param timestamp the timestamp to compare against
     * @return a list of organizations created after the specified timestamp
     */
    @Query("{'createdAt': {$gt: ?0}}")
    List<Organization> findOrganizationsCreatedAfter(long timestamp);
    
    /**
     * Custom query to find organizations with a specific industry
     * 
     * @param industry the industry to search for
     * @return a list of organizations in the specified industry
     */
    @Query("{'industry': ?0}")
    List<Organization> findByIndustry(String industry);
    
    /**
     * Custom query to find organizations with employee count greater than the specified value
     * 
     * @param count the minimum employee count
     * @return a list of organizations with more employees than the specified count
     */
    @Query("{'employeeCount': {$gt: ?0}}")
    List<Organization> findByEmployeeCountGreaterThan(int count);
}