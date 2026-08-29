package com.example.storeServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItemRequest {
    @JsonProperty(value = "order_id")
    private Long orderId;
    @JsonProperty(value = "item_id")
    private Long itemId;

    public OrderItemRequest(Long orderId, Long itemId) {
        this.orderId = orderId;
        this.itemId = itemId;
    }

    public OrderItemRequest() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                '}';
    }
}
