package com.jsf.migration.repository;

import com.jsf.migration.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods to interact with the Users collection in MongoDB.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username already exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by their role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    @Query("{'profile.role': ?0}")
    List<User> findByProfileRole(String role);

    /**
     * Find users who haven't logged in since a specific date.
     *
     * @param date the date threshold
     * @return a list of users who haven't logged in since the specified date
     */
    @Query("{'profile.lastLogin': {$lt: ?0}}")
    List<User> findByLastLoginBefore(Date date);

    /**
     * Find users by first name and last name.
     *
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return a list of users matching the specified first and last name
     */
    @Query("{'profile.firstName': ?0, 'profile.lastName': ?1}")
    List<User> findByProfileFirstNameAndLastName(String firstName, String lastName);

    /**
     * Search users by username or email containing the given text (case insensitive).
     *
     * @param searchText the text to search for
     * @return a list of users matching the search criteria
     */
    @Query("{'$or': [{'username': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    List<User> searchByUsernameOrEmail(String searchText);

    /**
     * Update a user's last login date.
     *
     * @param userId the ID of the user to update
     * @param lastLogin the new last login date
     */
    @Query(value = "{'_id': ?0}", fields = "{'profile.lastLogin': ?1}")
    void updateLastLogin(String userId, Date lastLogin);

    /**
     * Find users who have specific preferences.
     *
     * @param preferenceName the name of the preference
     * @param preferenceValue the value of the preference
     * @return a list of users with the specified preference
     */
    @Query("{'preferences.?0': ?1}")
    List<User> findByPreference(String preferenceName, Object preferenceValue);
}