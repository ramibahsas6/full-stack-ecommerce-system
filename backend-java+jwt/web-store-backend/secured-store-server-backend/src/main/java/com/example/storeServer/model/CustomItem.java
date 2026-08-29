package com.example.storeServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class CustomItem {
    private long id;
    private String title;
    private String description;
    @JsonProperty(value = "photo_url")
    private String photoUrl;
    @JsonProperty(value = "price_usd")
    private float priceUsd;
    private int stock;
    @JsonProperty(value = "created_at")
    private LocalDate createdAt;
    private boolean edited;
    @JsonProperty(value = "updated_at")
    private LocalDate updatedAt;

    public CustomItem(String title, String description, String photoUrl, float priceUsd, int stock) {
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.priceUsd = priceUsd;
        this.stock = stock;
    }

    public CustomItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public float getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(float priceUsd) {
        this.priceUsd = priceUsd;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
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
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", priceUsd=" + priceUsd +
                ", stock=" + stock +
                ", createdAt=" + createdAt +
                ", edited=" + edited +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
