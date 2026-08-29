package com.example.storeServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemResponse {
    @JsonProperty(value = "item_id")
    private Long itemId;
    @JsonProperty(value = "photo_url")
    private String photoUrl;
    private String title;
    @JsonProperty(value = "max_stock")
    private int maxStock;
    private int quantity;
    @JsonProperty(value = "price_usd")
    private float totalPriceAtPurchase;

    public ItemResponse(String photoUrl, String title, int quantity, float totalPriceAtPurchase) {
        this.photoUrl = photoUrl;
        this.title = title;
        this.quantity = quantity;
        this.totalPriceAtPurchase = totalPriceAtPurchase;
    }

    public ItemResponse() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getTotalPriceAtPurchase() {
        return totalPriceAtPurchase;
    }


    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPriceAtPurchase(float totalPriceAtPurchase) {
        this.totalPriceAtPurchase = totalPriceAtPurchase;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "ItemResponse{" +
                "itemId=" + itemId +
                ", photoUrl='" + photoUrl + '\'' +
                ", title='" + title + '\'' +
                ", maxStock=" + maxStock +
                ", quantity=" + quantity +
                ", totalPriceAtPurchase=" + totalPriceAtPurchase +
                '}';
    }
}
