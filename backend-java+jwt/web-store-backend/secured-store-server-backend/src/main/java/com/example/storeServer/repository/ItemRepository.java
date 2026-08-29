package com.example.storeServer.repository;
import com.example.storeServer.model.CustomItem;
import com.example.storeServer.repository.mapper.CustomItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ITEMS_TABLE = "items";

    // שאילתה בסיסית שמחזירה את כל המוצרים מהטבלה – לדף הבית, קטגוריות וכו'
    public List<CustomItem> getAllItems(){
        String sql = "SELECT * FROM items";
        return jdbcTemplate.query(sql, new CustomItemMapper());
    }

    // חיפוש לפי טקסט בכותרת – NOT case sensitive (משתמשים ב-LOWER)
    public List<CustomItem> searchAtItems(String search){
        String sql = String.format("SELECT * FROM %s WHERE LOWER(title) LIKE ?", ITEMS_TABLE);
        return jdbcTemplate.query(sql, new CustomItemMapper(), "%" + search.toLowerCase() + "%");
    }

    // מחזיר את מחיר הפריט (בדולרים) לפי ה-ID – שימוש לחישוב סכום בהזמנה
    public Float findPriceById(Long itemId){
        String sql = String.format("SELECT price_usd FROM %s WHERE id = ?", ITEMS_TABLE);
        return jdbcTemplate.queryForObject(sql, Float.class, itemId);
    }

    /// ///////// add item ////////////
    public int addItem(CustomItem item) {
        // בדיקה אם השדות null או לא תקינים – מניעת טעויות
        if(item.getTitle() == null || item.getPriceUsd() < 0 || item.getStock() < 0) {
            return 0;
        }

        String sql = "INSERT INTO items(title, description, photo_url, price_usd, stock, created_at, is_edited, updated_at) " +
                "VALUES(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, FALSE, CURRENT_TIMESTAMP)";
        return jdbcTemplate.update(sql,
                item.getTitle(),
                item.getDescription(),
                item.getPhotoUrl(),
                item.getPriceUsd(),
                item.getStock()
        );
    }

    /// ///////// update item ////////////
    public int updateItem(CustomItem item) {
        // בדיקה אם השדות null – לא ניתן לעדכן
        if(item.getId() < 0 || item.getTitle() == null || item.getPriceUsd() < 0 || item.getStock() < 0) {
            return 0;
        }

        String sql = "UPDATE items SET title = ?, description = ?, photo_url = ?, price_usd = ?, stock = ?, is_edited = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql,
                item.getTitle(),
                item.getDescription(),
                item.getPhotoUrl(),
                item.getPriceUsd(),
                item.getStock(),
                item.getId()
        );
    }

    /// ///////// delete item ////////////
    public int deleteItem(Long itemId) {
        // בדיקה אם itemId null
        if(itemId == null) {
            return 0;
        }

        String sql = "DELETE FROM items WHERE id = ?";
        return jdbcTemplate.update(sql, itemId);
    }

    /// //////////// helpers /////////////////////

    // מביא פריט אחד לפי ID – במקרה של שגיאה/לא נמצא, מחזיר null במקום לזרוק Exception
    public CustomItem getItemByIdHelper(Long id){
        try {
            String sql = "SELECT * FROM items WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, new CustomItemMapper(), id);
        }catch (Exception e){
            return null;
        }
    }

    // מחזיר את המלאי הנוכחי לפריט ונועל את השורה (FOR UPDATE) כדי למנוע התנגשויות בעדכון
    public Integer getStockByItemIdHelper(Long itemId){
        try{
            String sql = String.format("SELECT stock FROM %s WHERE id = ? FOR UPDATE", ITEMS_TABLE);
            return jdbcTemplate.queryForObject(sql, Integer.class, itemId);
        }catch (Exception e) {
            return null;
        }
    }

    // מעדכן את ערך המלאי לפריט מסוים – אחרי קנייה/החזרה/שינוי כמות
    public int updateItemStocksHelper(Long itemId, int stock){
        String sql = String.format("UPDATE %s SET stock = ? WHERE id = ?", ITEMS_TABLE);
        return jdbcTemplate.update(sql, stock, itemId);
    }
}
