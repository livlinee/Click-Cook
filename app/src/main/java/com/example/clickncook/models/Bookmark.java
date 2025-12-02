package com.example.clickncook.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class Bookmark implements Serializable {
    @Exclude private String id;
    private String userId;
    private String recipeId;
    @ServerTimestamp private Date createdAt;

    public Bookmark() {}

    public Bookmark(String userId, String recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    @Exclude public String getId() { return id; }
    @Exclude public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}