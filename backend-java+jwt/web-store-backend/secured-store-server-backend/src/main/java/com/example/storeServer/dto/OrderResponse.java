package com.example.storeServer.dto;

import com.example.storeServer.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderResponse {
    @JsonProperty(value = "order_id")
    private Long orderId;
    @JsonProperty(value = "items")
    private List<ItemResponse> itemResponses;
    @JsonProperty(value = "total_price")
    private float totalPrice;
    @JsonProperty(value = "order_status")
    private OrderStatus orderStatus;

    public OrderResponse(Long orderId, List<ItemResponse> itemResponses, float totalPrice, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.itemResponses = itemResponses;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public OrderResponse() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<ItemResponse> getItemResponses() {
        return itemResponses;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setItemResponses(List<ItemResponse> itemResponses) {
        this.itemResponses = itemResponses;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId=" + orderId +
                ", itemResponses=" + itemResponses +
                ", totalPrice=" + totalPrice +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
