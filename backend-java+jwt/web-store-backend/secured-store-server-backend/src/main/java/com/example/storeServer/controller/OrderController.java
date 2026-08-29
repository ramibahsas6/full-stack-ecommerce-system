package com.example.storeServer.controller;

import com.example.storeServer.dto.OrderResponse;
import com.example.storeServer.model.Order;
import com.example.storeServer.service.OrderService;
import com.example.storeServer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    /// ////////////// crud ///////////////////////

    // מחזיר הזמנה אחת לפי id למשתמש המחובר (מוציא username מתוך ה-JWT)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getOrderById(@RequestHeader(value = "Authorization") String token, @RequestParam Long id) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        Order order = orderService.getOrderById(username, id);
        return ResponseEntity.ok(order);
    }

    // מחזיר את כל ההזמנות (TEMP + CLOSE) של המשתמש המחובר כ-OrderResponse
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/all-orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestHeader(value = "Authorization") String token) {
        try{
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            List<OrderResponse> orders = orderService.getAllOrders(username);
            return ResponseEntity.ok(orders);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // יצירת הזמנה TEMP למשתמש – אם כבר קיימת אחת, מחזיר את ה-id שלה
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader(value = "Authorization") String token){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        Long orderId = orderService.createOrder(username);

        if(orderId == null)
            return ResponseEntity.internalServerError().body("server error");

        if(orderId < 0)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(orderId);

        return ResponseEntity.ok(orderId);
    }

    // סגירת הזמנה TEMP וקנייה בפועל – משנה ל-CLOSE ומעדכן מלאי
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public  ResponseEntity<?> buyOrder(@RequestHeader(value = "Authorization") String token){
        try{
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            Order order = orderService.buyOrder(username);
            if(order == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // מחיקת הזמנת TEMP של המשתמש (למשל "ריקון עגלה" לפני קנייה)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteOrder(@RequestHeader(value = "Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        int rows = orderService.deleteOrder(username);
        if(rows > 0)
            return ResponseEntity.ok(username + " you deleted your order temp successfully");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't delete order, because the order is not found");
    }
}