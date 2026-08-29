package com.example.storeServer.controller;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.model.CustomItem;
import com.example.storeServer.service.ItemService;
import com.example.storeServer.utils.JwtUtil;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtUtil jwtUtil;

    /// ///////////// crud ////////////////////////

    // נקודת קצה פתוחה – מחזירה את כל המוצרים (ללא קשר למשתמש)
    @PermitAll
    @GetMapping("/all")
    public List<CustomItem> getAllItems(){
        return itemService.getAllItems();
    }

    // מחזיר את כל הפריטים למשתמש מחובר – כולל כמות בעגלה TEMP אם קיימת
    @GetMapping("/user-items")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ItemResponse> getAllUserItems(@RequestHeader(value = "Authorization") String token){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        return itemService.getAllUserItems(username);
    }

    // חיפוש פריטים לפי search + page – כרגע משתמשים בזה בעיקר ל-"home"
    @GetMapping("/search/{page}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ItemResponse> getSearchedItems(@RequestHeader(value = "Authorization") String token, @PathVariable String page,@RequestParam String search) {

        System.out.println("1");
        String username = jwtUtil.extractUsername(token.substring(7));
        return itemService.getSearchedItems(username, page, search);
    }

    // נקודת קצה פתוחה – מחזירה מחיר של פריט לפי ID (שימושי לצד לקוח/מיקרוסרביסים)
    @PermitAll
    @GetMapping("/get_price")
    public float getPriceById(@RequestParam Long itemId){
        return itemService.getPriceById(itemId);
    }

          /// /////////////// שאר הקראד נמצא כאן בקומנטז למטה אבל הם פונקציות ששיכות רק ל ADMIN
//    // הוספת פריט חדש
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Integer> addItem(@RequestBody CustomItem item){
//        int rows = itemService.addItem(item);
//        if(rows > 0)
//            return ResponseEntity.ok(rows);
//
//        return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
//    }
//
//    // עדכון פריט קיים
//    @PutMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Integer> updateItem(@RequestBody CustomItem item){
//        int rows = itemService.updateItem(item);
//        if(rows > 0)
//            return ResponseEntity.ok(rows);
//
//        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
//    }
//
//    // מחיקת פריט
//    @DeleteMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Integer> deleteItem(@RequestParam Long itemId){
//        int rows = itemService.deleteItem(itemId);
//        if(rows > 0)
//            return ResponseEntity.ok(rows);
//
//        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
//    }
}