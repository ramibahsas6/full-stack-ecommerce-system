package com.example.storeServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {

    @JsonProperty(value = "order_id")
    private long orderId;
    @JsonProperty(value = "item_id")
    private long itemId;
    private int quantity;
    @JsonProperty(value = "price_at_purchase")
    private float priceAtPurchase;
    @JsonProperty(value = "total_price")
    private float totalPrice;

    public OrderItem(long orderId, long itemId, int quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public OrderItem() {
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(float priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
