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
     * Find an organization by its name.
     *
     * @param name the organization name to search for
     * @return an Optional containing the organization if found
     */
    Optional<Organization> findByName(String name);

    /**
     * Find organizations containing the given name pattern (case-insensitive).
     *
     * @param namePattern the pattern to search for in organization names
     * @return list of matching organizations
     */
    List<Organization> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Find organizations by address city.
     *
     * @param city the city to search for
     * @return list of organizations in the specified city
     */
    @Query("{'address.city': ?0}")
    List<Organization> findByAddressCity(String city);

    /**
     * Find organizations by address country.
     *
     * @param country the country to search for
     * @return list of organizations in the specified country
     */
    @Query("{'address.country': ?0}")
    List<Organization> findByAddressCountry(String country);

    /**
     * Check if an organization with the given name exists.
     *
     * @param name the organization name to check
     * @return true if an organization with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Count organizations by address country.
     *
     * @param country the country to count organizations for
     * @return the number of organizations in the specified country
     */
    @Query(value = "{'address.country': ?0}", count = true)
    long countByAddressCountry(String country);

    /**
     * Find organizations created after a specific date.
     *
     * @param date the date threshold
     * @return list of organizations created after the specified date
     */
    @Query("{'createdAt': {$gt: ?0}}")
    List<Organization> findByCreatedAtAfter(java.util.Date date);
}