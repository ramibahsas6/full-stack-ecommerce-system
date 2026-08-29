package com.example.storeServer.controller;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.service.FavoriteService;
import com.example.storeServer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/favorites")
@CrossOrigin(origins = "http://localhost:3000")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private JwtUtil jwtUtil;

    /// //////////////// crud /////////////////////////

    // מחזיר את כל המועדפים של המשתמש המחובר (לפי ה-JWT ב-Authorization)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ItemResponse>> getFavorites(@RequestHeader(value = "Authorization") String token)
    {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        List<ItemResponse> itemResponses = favoriteService.getFavorites(username);
        return ResponseEntity.ok(itemResponses);
    }

    // מוסיף פריט למועדפים של המשתמש – אם כבר קיים יחזיר 0
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> addToFavorites(@RequestHeader(value = "Authorization") String token, @RequestBody ItemResponse itemResponse){
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            System.out.println("from add : " + itemResponse);
            return ResponseEntity.ok(favoriteService.addToFavorites(username, itemResponse));
        }catch (Exception e){
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        }
    }

    // עדכון פריט מהמועדפים לפי המשתמש והפריט שנשלח בבקשה
    @PutMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> updateFavorite(@RequestHeader(value = "Authorization") String token, @RequestBody ItemResponse itemResponse){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);

        // קריאה ל-service לעדכון הפריט
        int rows = favoriteService.updateFavorite(username, itemResponse);

        if(rows > 0)
            return ResponseEntity.ok(rows);

        // אם לא נמצא או לא ניתן לעדכן – מחזיר 0
        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }

    // מחיקת פריט מהמועדפים לפי המשתמש והפריט שנשלח בבקשה
    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> deleteFromFavorites(@RequestHeader(value = "Authorization") String token, @RequestBody ItemResponse itemResponse){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        int rows = favoriteService.deleteFromFavorites(username, itemResponse);
        System.out.println(itemResponse);
        if(rows > 0)
            return ResponseEntity.ok(rows);

        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }
}