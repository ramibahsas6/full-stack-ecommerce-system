package com.example.storeServer.controller;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.dto.OrderItemRequest;
import com.example.storeServer.model.OrderItem;
import com.example.storeServer.service.OrderItemService;
import com.example.storeServer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/order-items")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private JwtUtil jwtUtil;

    /// ///////////////////// crud ///////////////////////////////

    // מביא פריט יחיד מתוך הזמנה (לפי orderId + itemId שנשלחים בבקשה)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderItem> getOrderItem(@RequestHeader(value = "Authorization") String token, @RequestBody OrderItemRequest orderItemRequest){

        OrderItem orderItem = orderItemService.getOrderItem(orderItemRequest);
        if(orderItem != null)
            return ResponseEntity.ok(orderItem);

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // מחזיר את כל הפריטים של הזמנה מסוימת (משמש בעיקר לדף ה־orders / modal)
    @GetMapping("/all-order-items/{order_id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderItem>> getAllOrderItems(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "order_id") Long orderId){
        try {
            List<OrderItem> orderItems = orderItemService.getAllOrderItems(orderId);
            return ResponseEntity.ok(orderItems);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // שינוי פריט בעגלה: is_from_create קובע אם זה הוספה ראשונה או שינוי כמות רגיל
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{is_from_create}")
    public ResponseEntity<ItemResponse> changeOrderItem(@RequestHeader(value = "Authorization") String token, @RequestBody ItemResponse itemResponse,@PathVariable(value = "is_from_create") boolean isFromCreate){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        ItemResponse itemResponse1 = orderItemService.changeOrderItem(username, itemResponse, isFromCreate);
        if(itemResponse1 != null)
            return ResponseEntity.ok(itemResponse1);

        return new ResponseEntity<>(null, HttpStatus.CONFLICT);
    }

    // עדכון ישיר של OrderItem (כרגע בשימוש פנימי, פחות לפרונט)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<OrderItem> updateOrderItem(@RequestHeader(value = "Authorization") String token, @RequestBody OrderItem orderItem) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        OrderItem orderItem1 = orderItemService.updateOrderItem(username, orderItem, true);
        if (orderItem1 != null)
            return ResponseEntity.ok(orderItem1);

        return new ResponseEntity<>(null , HttpStatus.CONFLICT);
    }

    // מחיקת פריט יחיד מה-order_items לפי הזמנה ופריט
    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> deleteOrderItem(@RequestHeader(value = "Authorization") String token, @RequestBody OrderItem orderItem){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken); // רק אם תרצה להשתמש ל-Security

        // קריאה ל-service למחיקה
        int rows = orderItemService.deleteOrderItem(orderItem.getOrderId(), orderItem.getItemId());

        if(rows > 0)
            return ResponseEntity.ok(rows);

        // אם לא נמצא או לא ניתן למחוק – מחזיר 0
        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }
}