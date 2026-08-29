package com.example.storeServer.repository.mapper;

import com.example.storeServer.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrderId(rs.getLong("order_id"));
        orderItem.setItemId(rs.getLong("item_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setPriceAtPurchase(rs.getFloat("price_at_purchase"));
        orderItem.setTotalPrice(rs.getFloat("total_price"));

        return orderItem;
    }
}
