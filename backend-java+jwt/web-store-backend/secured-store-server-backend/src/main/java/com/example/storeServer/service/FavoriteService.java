package com.example.storeServer.service;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.model.Favorite;
import com.example.storeServer.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ItemService itemService;

    // מחזיר רשימה של ItemResponse רק עבור הפריטים שהם מועדפים אצל המשתמש
    public List<ItemResponse> getFavorites(String username){
        // כל הפריטים הרגילים (כולל מידע על כמות בעגלה TEMP וכו')
        List<ItemResponse> itemResponses = itemService.getAllUserItems(username);
        List<ItemResponse> favoritesItemResponses = new ArrayList<>();
        // המועדפים הגולמיים מהדאטה־בייס (username + itemId)
        List<Favorite> favorites = favoriteRepository.findFavorites(username);

        // ממפים את כל ה-ItemResponse לפי itemId כדי שנוכל לשלוף אותם בקלות לפי רשימת המועדפים
        Map<Long, ItemResponse> updatesById = itemResponses.stream()
                .collect(Collectors.toMap(ItemResponse::getItemId, Function.identity()));

        // עוברים על רשימת המועדפים ומוציאים רק את הפריטים שקיימים במפה
        for (Favorite favorite : favorites) {
            Long id = favorite.getItemId();
            if (updatesById.containsKey(id)) {
                favoritesItemResponses.add(updatesById.get(id));
            }
        }
        return favoritesItemResponses;
    }

    // הוספת פריט למועדפים – לא מוסיף אם הוא כבר קיים (מחזיר 0 במקום)
    public int addToFavorites(String username, ItemResponse itemResponse){
        if (favoriteRepository.isFavoriteExistsHelper(username, itemResponse.getItemId()))
            return 0;

        return favoriteRepository.saveFavorite(username, itemResponse);
    }

    /// ///////// update favorite ////////////
    public int updateFavorite(String username, ItemResponse itemResponse) {
        Long itemId = itemResponse.getItemId();

        // בדיקה אם הנתונים null – מניעת טעויות
        if(username == null || itemId == null) {
            return 0;
        }

        // בדיקה אם הפריט קיים בטבלת favorites
        boolean exists = favoriteRepository.isFavoriteExistsHelper(username, itemId);
        if(!exists) {
            // אם לא קיים – לא ניתן לעדכן
            return 0;
        }

        // קריאה ל-repository לעדכון הפריט
        return favoriteRepository.updateFavorite(username, itemId, itemResponse);
    }

    /// ///////// delete favorite ////////////
    public int deleteFromFavorites(String username, ItemResponse itemResponse) {
        Long itemId = itemResponse.getItemId();

        // בדיקה אם הנתונים null – לא ניתן למחוק
        if(username == null || itemId == null) {
            return 0;
        }

        // החזרת מספר השורות שנמחקו
        return favoriteRepository.deleteFavorite(username, itemId);
    }
}