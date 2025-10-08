package com.example.migration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Profile entity representing extended user profile information stored in MongoDB.
 * Contains personal details and preferences beyond basic authentication data.
 */
@Document(collection = "profiles")
public class Profile {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("userId")
    private String userId;

    private String avatar;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{8,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    private Address address;

    private Map<String, String> socialLinks = new HashMap<>();

    private Map<String, Object> preferences = new HashMap<>();

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    /**
     * Nested class representing a physical address
     */
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        public Address() {
        }

        public Address(String street, String city, String state, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    /**
     * Default constructor
     */
    public Profile() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with userId
     * 
     * @param userId the ID of the associated user
     */
    public Profile(String userId) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Full constructor
     */
    public Profile(String userId, String avatar, String bio, String phoneNumber, 
                  Address address, Map<String, String> socialLinks, 
                  Map<String, Object> preferences) {
        this.userId = userId;
        this.avatar = avatar;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.socialLinks = socialLinks != null ? socialLinks : new HashMap<>();
        this.preferences = preferences != null ? preferences : new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Updates the updatedAt timestamp to current time
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds or updates a social media link
     * 
     * @param platform the social media platform name
     * @param url the profile URL
     */
    public void addSocialLink(String platform, String url) {
        if (this.socialLinks == null) {
            this.socialLinks = new HashMap<>();
        }
        this.socialLinks.put(platform, url);
        updateTimestamp();
    }

    /**
     * Removes a social media link
     * 
     * @param platform the social media platform to remove
     */
    public void removeSocialLink(String platform) {
        if (this.socialLinks != null) {
            this.socialLinks.remove(platform);
            updateTimestamp();
        }
    }

    /**
     * Sets a user preference
     * 
     * @param key the preference key
     * @param value the preference value
     */
    public void setPreference(String key, Object value) {
        if (this.preferences == null) {
            this.preferences = new HashMap<>();
        }
        this.preferences.put(key, value);
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", bio='" + (bio != null ? bio.substring(0, Math.min(bio.length(), 20)) + "..." : "null") + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}