package com.modernization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.util.Objects;

/**
 * Profile entity representing extended user profile information.
 * Stored in the Profiles collection in MongoDB.
 */
@Document(collection = "Profiles")
public class Profile {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    @Field("userId")
    private String userId;

    @Size(max = 1000)
    private String bio;

    private String avatar;

    private String phoneNumber;

    @Valid
    private Address address;

    private SocialMedia socialMedia;

    private String preferredLanguage;

    private String timezone;

    private String jobTitle;

    private String department;

    /**
     * Default constructor for Profile
     */
    public Profile() {
    }

    /**
     * Constructor with required fields
     * 
     * @param userId the ID of the associated user
     */
    public Profile(String userId) {
        this.userId = userId;
    }

    /**
     * Full constructor for Profile
     * 
     * @param id                the profile ID
     * @param userId            the ID of the associated user
     * @param bio               user biography
     * @param avatar            avatar image URL
     * @param phoneNumber       contact phone number
     * @param address           user address
     * @param socialMedia       social media links
     * @param preferredLanguage preferred language setting
     * @param timezone          user timezone
     * @param jobTitle          professional job title
     * @param department        organizational department
     */
    public Profile(String id, String userId, String bio, String avatar, String phoneNumber,
                  Address address, SocialMedia socialMedia, String preferredLanguage,
                  String timezone, String jobTitle, String department) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.socialMedia = socialMedia;
        this.preferredLanguage = preferredLanguage;
        this.timezone = timezone;
        this.jobTitle = jobTitle;
        this.department = department;
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

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id) &&
               Objects.equals(userId, profile.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
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
                ", socialMedia=" + socialMedia +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", timezone='" + timezone + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", department='" + department + '\'' +
                '}';
    }

    /**
     * Nested class representing a user's address
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
     * Nested class representing a user's social media links
     */
    public static class SocialMedia {
        private String linkedin;
        private String twitter;
        private String facebook;
        private String instagram;
        private String github;

        public SocialMedia() {
        }

        public SocialMedia(String linkedin, String twitter, String facebook, String instagram, String github) {
            this.linkedin = linkedin;
            this.twitter = twitter;
            this.facebook = facebook;
            this.instagram = instagram;
            this.github = github;
        }

        public String getLinkedin() {
            return linkedin;
        }

        public void setLinkedin(String linkedin) {
            this.linkedin = linkedin;
        }

        public String getTwitter() {
            return twitter;
        }

        public void setTwitter(String twitter) {
            this.twitter = twitter;
        }

        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String facebook) {
            this.facebook = facebook;
        }

        public String getInstagram() {
            return instagram;
        }

        public void setInstagram(String instagram) {
            this.instagram = instagram;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String github) {
            this.github = github;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SocialMedia that = (SocialMedia) o;
            return Objects.equals(linkedin, that.linkedin) &&
                   Objects.equals(twitter, that.twitter) &&
                   Objects.equals(facebook, that.facebook) &&
                   Objects.equals(instagram, that.instagram) &&
                   Objects.equals(github, that.github);
        }

        @Override
        public int hashCode() {
            return Objects.hash(linkedin, twitter, facebook, instagram, github);
        }

        @Override
        public String toString() {
            return "SocialMedia{" +
                    "linkedin='" + linkedin + '\'' +
                    ", twitter='" + twitter + '\'' +
                    ", facebook='" + facebook + '\'' +
                    ", instagram='" + instagram + '\'' +
                    ", github='" + github + '\'' +
                    '}';
        }
    }
}