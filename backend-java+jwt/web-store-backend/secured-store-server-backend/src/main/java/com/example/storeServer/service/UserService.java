package com.example.storeServer.service;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.model.Role;
import com.example.storeServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // שירות עסקים למשתמש – מטפל בלוגיקה לפני/אחרי הגישה לדאטה־בייס
    public int register(CustomUser user) {
        // ולידציה בסיסית – לוודא שכל השדות החיוניים לא null ולא ריקים
        if (    user.getUserName() == null || user.getUserName().isEmpty()
                || user.getFirstName() == null || user.getFirstName().isEmpty()
                || user.getLastName() == null || user.getLastName().isEmpty()
                || user.getEmail() == null || user.getEmail().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty() ) { // for checking the null or empty fields
            throw new IllegalArgumentException("User not registered, username, first name, last name, email and password are required and not empty");
        }

        // בדיקה שאין משתמש אחר עם אותו username
        CustomUser userWithTheSameUsername = getUserByUsernameHelper(user.getUserName());
        if(userWithTheSameUsername != null) // if there is user with the same username
            throw new IllegalArgumentException("User not registered, This username is already exists in the system.");

        // בדיקה שאין משתמש אחר עם אותו אימייל
        CustomUser userWithTheSameEmail = getUserByEmail(user.getEmail());
        if(userWithTheSameEmail != null) // if there is user with the same mail
            throw new IllegalArgumentException("User not registered, This email is already exists in the system.");

        // הצפנת הסיסמה לפני שמירה בדאטה־בייס
        user.setPassword(passwordEncoder.encode(user.getPassword())); // to encode the password for save in the DB
        // System.out.println("Encoded password: " + user.getPassword()); // Log the encoded password

        // ברירת מחדל – כל משתמש שלא הגדיר ADMIN נהיה USER רגיל
        if (user.getRole() == null || !user.getRole().name().equals("ADMIN")) { // if the user send null role
            user.setRole(Role.USER);
        }

        return userRepository.register(user);
    }

    public CustomUser getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public List<CustomUser> getAllUsers() {
        return userRepository.findAllUsers();
    }

    // עדכון משתמש – כולל בדיקות ולידציה ויחודיות אימייל
    public CustomUser updateUser(CustomUser updatedUser) {

        if(updatedUser.getUserName() == null) // if sent username = null
            throw new IllegalArgumentException("can't update user, username must be not null");

        // בדיקה שכל השדות הדרושים הגיעו ולא ריקים
        if( // check if not null or empty fields
                updatedUser.getFirstName() == null || updatedUser.getFirstName().isEmpty() ||
                        updatedUser.getLastName() == null || updatedUser.getLastName().isEmpty() ||
                        updatedUser.getEmail() == null || updatedUser.getEmail().isEmpty() ||
                        updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty() ||
                        updatedUser.getRole() == null || updatedUser.getRole().name().isEmpty()
        )
            throw new IllegalArgumentException("can't update user, fields must be given and must be not empty");

        // אם יש אימייל זהה אצל משתמש אחר (לא אותו username) – זורקים שגיאה
        if(!userRepository.existsEmailAtAnotherRowHelper(updatedUser.getUserName(), updatedUser.getEmail()).isEmpty()) // for unique email, true if exist in another row
            throw new IllegalArgumentException("can't update user, email already exist");

//        CustomUser userFromDB = getUserByUsernameHelper(updatedUser.getUserName());
//        if(!userFromDB.getEmail().equals(updatedUser.getEmail())){
//            CustomUser userWithTheSameEmail = getUserByEmail(updatedUser.getEmail());
//            if(userWithTheSameEmail != null)
//                throw new IllegalArgumentException("can't update user, email already exist");
//        }

        return userRepository.updateUser(updatedUser);
    }

    public int deleteUser(String username) {
        return userRepository.deleteUser(username);
    }

    /// ///////////////   helpers ///////////////////

    // מתודות עזר שלא נחשפות החוצה – לשימוש פנימי של ה־service
    public CustomUser getUserByUsernameHelper(String username) {
        return userRepository.findUserByUsernameHelper(username);
    }

    public CustomUser getUserByEmail(String email) {
        return userRepository.findUserByEmailHelper(email);
    }

    public int deleteUserByUsername(String username) {
        if(!userRepository.existsByUsername(username)) {
            return 0; // המשתמש לא קיים
        }
        return userRepository.deleteByUsername(username); // מחזיר מספר רשומות שנמחקו
    }

}