package com.example.storeServer.controller;

import com.example.storeServer.model.CustomItem;
import com.example.storeServer.model.CustomUser;
import com.example.storeServer.service.ItemService;
import com.example.storeServer.service.UserService;
import com.example.storeServer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/all-users")
    public ResponseEntity<List<CustomUser>> getAllUsers() {
        try{
            List<CustomUser> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // הוספת פריט חדש
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> addItem(@RequestBody CustomItem item){
        int rows = itemService.addItem(item);
        if(rows > 0)
            return ResponseEntity.ok(rows);

        return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
    }

    // עדכון פריט קיים
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> updateItem(@RequestBody CustomItem item){
        int rows = itemService.updateItem(item);
        if(rows > 0)
            return ResponseEntity.ok(rows);

        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }

    // מחיקת פריט
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> deleteItem(@RequestParam Long itemId){
        int rows = itemService.deleteItem(itemId);
        if(rows > 0)
            return ResponseEntity.ok(rows);

        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }

    // מחיקת משתמש מחובר לפי ה־JWT – כולל כל מה שתלוי בו לפי ה־service
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        int rows = userService.deleteUserByUsername(username);
        if (rows > 0) {
            return ResponseEntity.ok("User " + username + " deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cannot delete user, username " + username + " not found.");

    }
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/delete-user/{username}")
//    public ResponseEntity<String> deleteAnotherUser(@PathVariable String username) {
//        try {
//            String result = userService.deleteUser(username);
//            if (result.contains("successfully")) {
//                return new ResponseEntity(result, HttpStatus.OK);
//            }
//            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}

