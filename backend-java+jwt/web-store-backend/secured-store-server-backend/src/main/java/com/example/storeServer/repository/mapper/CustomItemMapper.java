package com.example.storeServer.repository.mapper;

import com.example.storeServer.model.CustomItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomItemMapper implements RowMapper<CustomItem> {
    @Override
    public CustomItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomItem item = new CustomItem();
        item.setId(rs.getLong("id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setPhotoUrl(rs.getString("photo_url"));
        item.setPriceUsd(rs.getFloat("price_usd"));
        item.setStock(rs.getInt("stock"));
        item.setCreatedAt(rs.getDate("created_at").toLocalDate());
        item.setEdited(rs.getBoolean("is_edited"));
        item.setUpdatedAt(rs.getDate("updated_at").toLocalDate());

        return item;
    }
}
