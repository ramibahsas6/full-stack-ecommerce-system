package com.example.storeServer.repository.mapper;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.model.Role;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<CustomUser> {
    @Override
    public CustomUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomUser user = new CustomUser();
        user.setUserName(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setCountry(rs.getString("country"));
        user.setCity(rs.getString("city"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getDate("created_at").toLocalDate());
        user.setEdited(rs.getBoolean("is_edited"));
        user.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        user.setRole(Role.valueOf(rs.getString("role")));
        return user;
    }
}

