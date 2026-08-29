package com.example.storeServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Order {
    private long id;
    private String username;
    @JsonProperty(value = "order_date")
    private LocalDate orderDate;
    @JsonProperty(value = "shipping_country")
    private String shippingCountry;
    @JsonProperty(value = "shipping_city")
    private String shippingCity;
    @JsonProperty(value = "total_price")
    private float totalPrice;
    private OrderStatus status;

    public Order(String shippingCountry, String shippingCity) {
        this.shippingCountry = shippingCountry;
        this.shippingCity = shippingCity;
    }

    public Order() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", orderDate=" + orderDate +
                ", shippingCountry='" + shippingCountry + '\'' +
                ", shippingCity='" + shippingCity + '\'' +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}
