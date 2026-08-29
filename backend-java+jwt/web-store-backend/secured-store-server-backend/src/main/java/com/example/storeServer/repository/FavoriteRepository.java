package com.example.storeServer.repository;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.model.Favorite;
import com.example.storeServer.repository.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FAVORITES_TABLE = "favorites";

    // מחזיר את כל המועדפים של משתמש מסוים (רק כאלה שסומנו כ-edited = true)
    public List<Favorite> findFavorites(String username){
        String sql = String.format("select * FROM %s WHERE username = ? AND edited = ?", FAVORITES_TABLE);
        return jdbcTemplate.query(sql, new FavoriteMapper(), username, true);
    }

    // שומר פריט חדש בטבלת המועדפים עבור משתמש
    public int saveFavorite(String username, ItemResponse itemResponse){
        String sql = String.format("INSERT INTO %s (username,item_id,edited) VALUES(?,?,?)", FAVORITES_TABLE);
        return jdbcTemplate.update(sql, username, itemResponse.getItemId(), true);
    }

    /// ///////// update favorite ////////////
    public int updateFavorite(String username, Long itemId, ItemResponse itemResponse) {
        // בדיקה אם הנתונים null
        if(username == null || itemId == null) {
            return 0;
        }

        // עדכון כל השדות בטבלה favorites – עדכון אדיטד וטיימסטמפ
        String sql = "UPDATE favorites SET edited = ?, updated_at = CURRENT_TIMESTAMP WHERE username = ? AND item_id = ?";
        return jdbcTemplate.update(sql, true, username, itemId);
    }

    // מוחק פריט מסוים מהמועדפים של משתמש
    public int deleteFavorite(String username, Long itemId){
        String sql = String.format("DELETE FROM %s WHERE username = ? AND item_id = ?", FAVORITES_TABLE);
        return jdbcTemplate.update(sql, username, itemId);
    }

    /// ///////////////helpers /////////////////////////////////////

    // בודק אם פריט מסוים כבר קיים במועדפים של המשתמש (למניעת כפילויות)
    public Boolean isFavoriteExistsHelper(String username, Long itemId) {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE username = ? AND item_id = ?", FAVORITES_TABLE);
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, itemId);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

}