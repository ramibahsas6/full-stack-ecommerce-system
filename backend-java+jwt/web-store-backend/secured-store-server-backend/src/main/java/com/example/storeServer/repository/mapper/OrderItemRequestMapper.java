package com.example.storeServer.repository.mapper;

import com.example.storeServer.dto.OrderItemRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRequestMapper implements RowMapper<OrderItemRequest> {
    @Override
    public OrderItemRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setItemId(rs.getLong("order_id"));
        orderItemRequest.setItemId(rs.getLong("item_id"));

        return orderItemRequest;
    }
}
