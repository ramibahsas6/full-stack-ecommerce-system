package com.example.storeServer.repository.mapper;

import com.example.storeServer.model.Order;
import com.example.storeServer.model.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUsername(rs.getString("username"));
        order.setOrderDate(rs.getDate("order_date").toLocalDate());
        order.setShippingCountry(rs.getString("shipping_country"));
        order.setShippingCity(rs.getString("shipping_city"));
        order.setTotalPrice(rs.getFloat("total_price"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return order;
    }
}
