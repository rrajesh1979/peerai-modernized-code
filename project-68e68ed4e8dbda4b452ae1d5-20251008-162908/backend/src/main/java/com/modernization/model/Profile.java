package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Profile entity representing extended user profile information.
 * Maps to the 'profiles' collection in MongoDB.
 */
@Document(collection = "profiles")
public class Profile {

    @Id
    private String id;

    @NotNull(message = "User ID is required")
    @Indexed(unique = true)
    private String userId;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    private String avatar;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private Address address;

    private String[] socialLinks;

    private String[] skills;

    private String[] interests;

    private String preferredLanguage;

    private String timezone;

    private boolean publicProfile;

    /**
     * Nested class representing a physical address.
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return Objects.equals(street, address.street) &&
                   Objects.equals(city, address.city) &&
                   Objects.equals(state, address.state) &&
                   Objects.equals(zipCode, address.zipCode) &&
                   Objects.equals(country, address.country);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, city, state, zipCode, country);
        }

        @Override
        public String toString() {
            return "Address{" +
                   "street='" + street + '\'' +
                   ", city='" + city + '\'' +
                   ", state='" + state + '\'' +
                   ", zipCode='" + zipCode + '\'' +
                   ", country='" + country + '\'' +
                   '}';
        }
    }

    /**
     * Default constructor for Profile.
     */
    public Profile() {
    }

    /**
     * Constructor with required fields.
     *
     * @param userId the ID of the user this profile belongs to
     */
    public Profile(String userId) {
        this.userId = userId;
        this.publicProfile = false;
    }

    /**
     * Full constructor for Profile.
     *
     * @param userId the ID of the user this profile belongs to
     * @param bio user biography
     * @param avatar avatar image URL
     * @param phoneNumber user phone number
     * @param address user address
     * @param socialLinks array of social media links
     * @param skills array of user skills
     * @param interests array of user interests
     * @param preferredLanguage user's preferred language
     * @param timezone user's timezone
     * @param publicProfile whether the profile is public
     */
    public Profile(String userId, String bio, String avatar, String phoneNumber, 
                  Address address, String[] socialLinks, String[] skills, 
                  String[] interests, String preferredLanguage, String timezone, 
                  boolean publicProfile) {
        this.userId = userId;
        this.bio = bio;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.socialLinks = socialLinks;
        this.skills = skills;
        this.interests = interests;
        this.preferredLanguage = preferredLanguage;
        this.timezone = timezone;
        this.publicProfile = publicProfile;
    }

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String[] getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String[] socialLinks) {
        this.socialLinks = socialLinks;
    }

    public String[] getSkills() {
        return skills;
    }

    public void setSkills(String[] skills) {
        this.skills = skills;
    }

    public String[] getInterests() {
        return interests;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return publicProfile == profile.publicProfile &&
               Objects.equals(id, profile.id) &&
               Objects.equals(userId, profile.userId) &&
               Objects.equals(bio, profile.bio) &&
               Objects.equals(avatar, profile.avatar) &&
               Objects.equals(phoneNumber, profile.phoneNumber) &&
               Objects.equals(address, profile.address) &&
               Objects.equals(preferredLanguage, profile.preferredLanguage) &&
               Objects.equals(timezone, profile.timezone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, bio, avatar, phoneNumber, address, 
                           preferredLanguage, timezone, publicProfile);
    }

    @Override
    public String toString() {
        return "Profile{" +
               "id='" + id + '\'' +
               ", userId='" + userId + '\'' +
               ", bio='" + bio + '\'' +
               ", avatar='" + avatar + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", address=" + address +
               ", preferredLanguage='" + preferredLanguage + '\'' +
               ", timezone='" + timezone + '\'' +
               ", publicProfile=" + publicProfile +
               '}';
    }
}