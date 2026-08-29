package com.example.storeServer.controller;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.service.UserService;
import com.example.storeServer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /// //////////// crud ////////////////////////////////

    // מחזיר את פרטי המשתמש לפי ה־JWT שב־Authorization header
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getUserByUsername(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7); // מורידים את ה-"Bearer "
            String username = jwtUtil.extractUsername(jwtToken);
            CustomUser user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity<>("can't get, user with username " + jwtUtil.extractUsername(token.substring(7)) + " is not exist" ,HttpStatus.NOT_FOUND);
        }
    }

    // רישום משתמש חדש למערכת – מחזיר טקסט לפי הצלחה/כישלון
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody CustomUser user) {
        try {
            int rows = userService.register(user);

            if(rows > 0)
                return ResponseEntity.status(HttpStatus.CREATED).body("the user registered successfully");
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("the user can't be registered");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // עדכון פרטי משתמש על בסיס ה־JWT – אי אפשר לעדכן משתמש אחר
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<CustomUser> updateUser(@RequestHeader(value = "Authorization") String token, @RequestBody CustomUser updatedUser) {
        try{
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            updatedUser.setUserName(username); // מכריחים שהעדכון יהיה רק על המשתמש שמחובר כרגע
            CustomUser user = userService.updateUser(updatedUser);
            if(user == null)// this check is bonus for hackers if there try to update another user at anyway possible
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(new CustomUser(), HttpStatus.BAD_REQUEST);
        }
    }

    // מחיקת משתמש מחובר לפי ה־JWT – כולל כל מה שתלוי בו לפי ה־service
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestHeader(value = "Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        int rows = userService.deleteUser(username);
        if(rows > 0)
            return ResponseEntity.ok(username + " you deleted yourself successfully with all your orders and favorites");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't delete user, because the username " + username + " not found");
    }
}