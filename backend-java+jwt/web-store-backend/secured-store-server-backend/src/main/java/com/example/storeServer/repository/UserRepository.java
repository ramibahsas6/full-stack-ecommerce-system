package com.example.storeServer.repository;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String USERS_TABLE = "users";

    // ריפוזיטורי שמנהל את כל הגישה לטבלת המשתמשים בדאטה־בייס
    public int register(CustomUser user) {
        String sql = String.format("INSERT INTO %s (username, first_name, last_name, email, phone, country, city, password, role) VALUES (?,?,?,?,?,?,?,?,?)", USERS_TABLE);
        return jdbcTemplate.update(sql,user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getCountry()
                , user.getCity(), user.getPassword(), user.getRole().name());
    }

    // מחזיר משתמש לפי username – אם לא קיים תיזרק שגיאה
    public CustomUser findUserByUsername(String username) {
        String sql = String.format("SELECT * FROM %s WHERE username = ?", USERS_TABLE);
        return jdbcTemplate.queryForObject(sql, new UserMapper(), username);
    }

    // גרסה "בטוחה" יותר – אם אין משתמש מחזיר null במקום שגיאה
    public CustomUser findUserByUsernameHelper(String username) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE username = ?", USERS_TABLE);
            return jdbcTemplate.queryForObject(sql, new UserMapper(), username);
        } catch (Exception e){
            return null;
        }
    }

    // בדיקה האם יש משתמש אחר עם אותו אימייל
    public CustomUser findUserByEmailHelper(String email) {
        try{
            String sql = String.format("SELECT * FROM %s WHERE email = ?", USERS_TABLE);
            return jdbcTemplate.queryForObject(sql, new UserMapper(), email);
        } catch (Exception e){
            return null;
        }
    }

    // מביא את כל המשתמשים מהטבלה
    public List<CustomUser> findAllUsers() {
        String sql = String.format("SELECT * FROM %s", USERS_TABLE);
        return jdbcTemplate.query(sql, new UserMapper());
    }

    // עדכון פרטי משתמש + סימון שנערכה עריכה ושמירת זמן עדכון
    public CustomUser updateUser(CustomUser user) {
        String sql = String.format("UPDATE %s SET first_name = ?, last_name = ?, email = ?, phone = ?, country = ?, city = ?, is_edited = ?, updated_at = ? WHERE username = ?", USERS_TABLE);
        int rows = jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getCountry(), user.getCity(), true, Timestamp.valueOf(LocalDateTime.now()),user.getUserName());
        if (rows <= 0)
            return null;
        return findUserByUsernameHelper(user.getUserName());
    }

    // מחיקת משתמש לפי username (כולל מה שתלוי בו לפי הגדרות DB)
    public int deleteUser(String username) {
        String sql = String.format("DELETE FROM %s WHERE username = ?", USERS_TABLE);
        return jdbcTemplate.update(sql, username);

    }

    /// //////////// helpers ///////////////////////////////////

    // בודק אם האימייל כבר קיים אצל משתמש אחר (לא אותו username)
    public List<String> existsEmailAtAnotherRowHelper(String username, String email){
        String sql = "SELECT username FROM users where username <> ? AND email = ?";
        return jdbcTemplate.queryForList(sql, String.class, username, email);
    }

    // מחיקת משתמש לפי username, מחזיר מספר רשומות שנמחקו
    public int deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        return jdbcTemplate.update(sql, username);
    }

    // בדיקה אם המשתמש קיים
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}