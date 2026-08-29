package com.example.storeServer.repository;

import com.example.storeServer.dto.OrderItemRequest;
import com.example.storeServer.model.OrderItem;
import com.example.storeServer.repository.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ORDER_ITEMS_TABLE = "order_items";

    // מחפש רשומה אחת של פריט בהזמנה לפי (order_id + item_id)
    public OrderItem findOrderItem(OrderItemRequest orderItemRequest) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
            return jdbcTemplate.queryForObject(sql, new OrderItemMapper(), orderItemRequest.getOrderId(), orderItemRequest.getItemId());
        } catch (Exception e) {
            return null;
        }
    }

    // מחזיר את כל הפריטים של הזמנה מסוימת
    public List<OrderItem> findAllItemsByOrderId(Long orderId){
        String sql = String.format("SELECT * FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
        return jdbcTemplate.query(sql, new OrderItemMapper(), orderId);
    }

    // יצירת פריט חדש בטבלת order_items ולהחזיר אותו אחרי השמירה
    public OrderItem create(OrderItem orderItem) {
        String sql = String.format("INSERT INTO %s (order_id, item_id, quantity, price_at_purchase, total_price) VALUES(?,?,?,?,?)", ORDER_ITEMS_TABLE);
        int rows = jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getItemId(), orderItem.getQuantity(), orderItem.getPriceAtPurchase(), orderItem.getTotalPrice());
        if(rows > 0)
        {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setOrderId(orderItem.getOrderId());
            orderItemRequest.setItemId(orderItem.getItemId());
            return findOrderItem(orderItemRequest);
        }

        return null;
    }

    // עדכון quantity ו־total_price עבור פריט קיים בהזמנה
    public OrderItem update(OrderItem orderItem){
        String sql = String.format("UPDATE %s SET quantity = ?, total_price = ? WHERE order_id = ? And item_id = ?", ORDER_ITEMS_TABLE);
        int rows = jdbcTemplate.update(sql, orderItem.getQuantity() , orderItem.getQuantity() * orderItem.getPriceAtPurchase(), orderItem.getOrderId(), orderItem.getItemId());
        if(rows > 0)
        {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setOrderId(orderItem.getOrderId());
            orderItemRequest.setItemId(orderItem.getItemId());
            return findOrderItem(orderItemRequest);
        }

        return null;
    }

    // מחיקת פריט ספציפי מתוך הזמנה (לפי order_id + item_id)
    public int deleteOrderItem(Long orderId, Long itemId){
        String sql = String.format("DELETE FROM %s WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
        return jdbcTemplate.update(sql, orderId, itemId);
    }

    /// ////////////////////// helpers /////////////////////////

    // בדיקה אם פריט כבר קיים בהזמנה (משמש כדי למנוע כפילויות)
    public List<OrderItem> isUniqueOrderItemHelper(Long orderId, Long itemId){
        String sql = String.format("SELECT * FROM %s WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
        return jdbcTemplate.query(sql, new OrderItemMapper(), orderId, itemId);
    }

    // סכום כולל של כל הפריטים בהזמנה (total_price)
    public float getTotalPriceHelper(Long orderId){
        try {
            String sql = String.format("SELECT SUM(total_price) FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
            return jdbcTemplate.queryForObject(sql, Float.class, orderId);
        }catch (Exception e){
            return 0;
        }

    }

    // מחזיר את ה־quantity של פריט מסוים בהזמנה
    public int getQuantityHelper(OrderItem orderItem){
        try {
            String sql = String.format("SELECT quantity FROM %s WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
            return jdbcTemplate.queryForObject(sql, Integer.class, orderItem.getOrderId(), orderItem.getItemId());
        }catch (Exception e){
            return -1;
        }
    }

    // רשימת כמויות לכל הפריטים בהזמנה (עם FOR UPDATE לנעילת שורות)
    public List<Integer> getListOfQuantitiesHelper(Long orderId){
        String sql = String.format("SELECT quantity FROM %s WHERE order_id = ? ORDER BY item_id FOR UPDATE", ORDER_ITEMS_TABLE);
        return jdbcTemplate.queryForList(sql, Integer.class, orderId);
    }

    // רשימת מזהי הפריטים בהזמנה (עם FOR UPDATE)
    public List<Long> getListOfItemsIdsHelper(Long orderId){
        String sql = String.format("SELECT item_id FROM %s WHERE order_id = ? ORDER BY item_id FOR UPDATE", ORDER_ITEMS_TABLE);
        return jdbcTemplate.queryForList(sql, Long.class, orderId);
    }

    // מחיר ליחידה (price_at_purchase) עבור פריט בהזמנה
    public Float getUnitPriceHelper(Long orderId, Long itemId){
        String sql = String.format("SELECT price_at_purchase FROM %s WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
        return jdbcTemplate.queryForObject(sql, Float.class, orderId, itemId);
    }

    // עדכון כמות חדשה וסכום כולל חדש לפריט אחרי שינויי מלאי
    public int updateNewQuantitiesAndNewTotalPricesHelper(Long orderId, Long itemId, Integer newQuantity, Float newTotalPrice){
        String sql = String.format("UPDATE %s SET quantity = ? , total_price = ? WHERE order_id = ? AND item_id = ?", ORDER_ITEMS_TABLE);
        return jdbcTemplate.update(sql, newQuantity, newTotalPrice, orderId, itemId);
    }

}