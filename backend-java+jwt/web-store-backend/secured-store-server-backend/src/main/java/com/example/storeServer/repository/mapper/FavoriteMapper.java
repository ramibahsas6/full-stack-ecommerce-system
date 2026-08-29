package com.example.storeServer.repository.mapper;

import com.example.storeServer.model.Favorite;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoriteMapper implements RowMapper<Favorite> {
    @Override
    public Favorite mapRow(ResultSet rs, int rowNum) throws SQLException {
        Favorite favorite = new Favorite();
        favorite.setUsername(rs.getString("username"));
        favorite.setItemId(rs.getLong("item_id"));
        favorite.setAddedAt(rs.getDate("added_at").toLocalDate());
        favorite.setEdited(rs.getBoolean("edited"));
        favorite.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        return favorite;
    }
}

