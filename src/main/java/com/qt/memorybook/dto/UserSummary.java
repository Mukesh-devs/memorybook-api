package com.qt.memorybook.dto;

// This class is a "Data Transfer Object". It defines how we send user
// information publicly over the API, without exposing sensitive data.
public class UserSummary {
    private Long id;
    private String username;
    private String displayName;
    private String profilePictureUrl;

    public UserSummary(Long id, String username, String displayName, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
