package com.example.clickncook.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    @Exclude private String id;
    private String email;
    private String name;
    private String bio;
    private String photoUrl;
    private String role;
    private boolean isBlocked;
    private int totalRecipes;
    @ServerTimestamp private Date createdAt;

    public User() {}

    public User(String email, String name, String bio, String photoUrl, String role) {
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.photoUrl = photoUrl;
        this.role = role;
        this.isBlocked = false;
        this.totalRecipes = 0;
    }

    // Getter & Setter
    @Exclude public String getId() { return id; }
    @Exclude public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    public int getTotalRecipes() { return totalRecipes; }
    public void setTotalRecipes(int totalRecipes) { this.totalRecipes = totalRecipes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}