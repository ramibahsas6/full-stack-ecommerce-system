package com.example.storeServer.repository;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.model.Order;
import com.example.storeServer.model.OrderStatus;
import com.example.storeServer.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ORDERS_TABLE = "orders";

    // מחזיר הזמנה לפי username + id, במקרה של שגיאה/לא נמצא מחזיר null
    public Order findOrderById(String username, Long id){
        try {
            String sql = String.format("SELECT * FROM %s WHERE username = ? AND id = ?", ORDERS_TABLE);
            return jdbcTemplate.queryForObject(sql, new OrderMapper(), username, id);
        }catch (Exception e){
            return null;
        }
    }

    // כל ההזמנות של משתמש מסוים לפי username
    public List<Order> findAllOrders(String username){
        String sql = String.format("SELECT * FROM %s WHERE username = ?", ORDERS_TABLE);
        return jdbcTemplate.query(sql, new OrderMapper(), username);
    }

    // יצירת הזמנה חדשה במצב TEMP לפי פרטי המשתמש (כולל מדינה/עיר לשילוח)
    public int saveOrder(CustomUser user){
        String sql = String.format("INSERT INTO %s (username,shipping_country,shipping_city,status) VALUES(?,?,?,?)",ORDERS_TABLE);
        return jdbcTemplate.update(sql ,user.getUserName(), user.getCountry(), user.getCity(), OrderStatus.TEMP.name());
    }

    // עדכון הזמנה לסגורה (CLOSE) עם totalPrice – מחזיר את ההזמנה המעודכנת
    public Order update(String username, Long orderId, float totalPrice){
        String sql = String.format("UPDATE %s SET total_price = ? , status = ? WHERE id = ?",ORDERS_TABLE);
        int rows = jdbcTemplate.update(sql ,totalPrice, OrderStatus.CLOSE.name(), orderId);
        if(rows > 0)
            return findOrderById(username, orderId);

        return null;
    }

    // מחיקת הזמנה לפי ה-id שלה
    public int deleteOrder(Long orderId) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", ORDERS_TABLE);
        return jdbcTemplate.update(sql, orderId);
    }

    /// ////////////////////// helpers //////////////////////////////////

    // מחזיר את ה-id של הזמנה TEMP לפי username, עם FOR UPDATE כדי לנעול את השורה בזמן עבודה
    public Long FindOrderIdWithStatusTempByUsernameHelper(String username){
        try {
            String sql = String.format("SELECT id FROM %s WHERE username = ? AND status = ? FOR UPDATE", ORDERS_TABLE);
            return jdbcTemplate.queryForObject(sql, Long.class, username, "TEMP");
        }catch (Exception e){
            return null;
        }
    }

    // בדיקה האם קיימת בכלל הזמנה TEMP למשתמש
    public boolean isOrderWithStatusTempExistHelper(String username){
        String sql = String.format("SELECT * FROM %s WHERE username = ? AND status = ?",ORDERS_TABLE);
        List<Order> orders = jdbcTemplate.query(sql, new OrderMapper(), username, OrderStatus.TEMP.name());
        return !orders.isEmpty();
    }

    // כל ה-id של ההזמנות שנמצאות כרגע במצב TEMP (לשימוש בעדכון כללי של מלאי)
    public List<Long> getAllOrdersIdsTemp(){
        String sql = String.format("SELECT id FROM %s WHERE status = ?", ORDERS_TABLE);
        return jdbcTemplate.queryForList(sql, Long.class, OrderStatus.TEMP.name());
    }

    // מחזיר id של הזמנה TEMP כלשהי (לשימוש נקודתי כשלא משנה של מי)
    public Long getOrderIdWithStatusTemp(){
        String sql = String.format("SELECT id FROM %s WHERE status = ?", ORDERS_TABLE);
        return jdbcTemplate.queryForObject(sql , Long.class, OrderStatus.TEMP.name());
    }

    // מחזיר את ה-total_price להזמנה לפי ה-id (אם אין/שגיאה – מחזיר null)
    public Float FindTotalPriceByOrderIdHelper(Long orderId) {
        String sql = String.format("SELECT total_price FROM %s WHERE id = ?", ORDERS_TABLE);

        try {
            return jdbcTemplate.queryForObject(sql, Float.class, orderId);
        }
        catch (Exception e) {
            return null;
        }
    }
}