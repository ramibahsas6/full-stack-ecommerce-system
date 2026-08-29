package com.example.storeServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Favorite {
    private String username;
    @JsonProperty(value = "item_id")
    private Long itemId;
    @JsonProperty(value = "added_at")
    private LocalDate addedAt;
    private boolean edited;
    @JsonProperty(value = "updated_at")
    private LocalDate updatedAt;

    public Favorite(String username ,Long itemId) {

        this.username = username;
        this.itemId = itemId;
    }

    public Favorite() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "username='" + username + '\'' +
                ", itemId=" + itemId +
                ", addedAt=" + addedAt +
                ", edited=" + edited +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
