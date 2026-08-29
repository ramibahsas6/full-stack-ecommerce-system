package com.example.storeServer.service;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.dto.OrderResponse;
import com.example.storeServer.model.CustomItem;
import com.example.storeServer.model.OrderItem;
import com.example.storeServer.model.OrderStatus;
import com.example.storeServer.repository.ItemRepository;
import com.example.storeServer.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderRepository orderRepository;

    // מחזיר את כל המוצרים כמו שהם מהדאטה־בייס (לשימוש גולמי)
    public List<CustomItem> getAllItems() {
        return itemRepository.getAllItems();
    }

    // מחזיר את כל הפריטים שצריך להציג למשתמש (כולל כמויות בעגלה TEMP אם קיימת)
    public List<ItemResponse> getAllUserItems(String username) {
        // כל ההזמנות של המשתמש (כולל TEMP ו-CLOSE)
        List<OrderResponse> orderResponses = orderService.getAllOrders(username);
        // כל הפריטים שקיימים במערכת
        List<CustomItem> customItems = getAllItems();
        // רשימת התשובות הסופית שנשלחת לפרונט
        List<ItemResponse> allItemResponses = new ArrayList<>();
        boolean isTempExist = false;
        OrderResponse tempOrder = null;

        // קודם בונים רשימה בסיסית לכל הפריטים – quantity = 0, stock מלא
        for (CustomItem customItem : customItems) {
            ItemResponse oldItemResponse = new ItemResponse();
            oldItemResponse.setItemId(customItem.getId());
            oldItemResponse.setPhotoUrl(customItem.getPhotoUrl());
            oldItemResponse.setTitle(customItem.getTitle());
            oldItemResponse.setQuantity(0);
            oldItemResponse.setMaxStock(customItem.getStock());
            oldItemResponse.setTotalPriceAtPurchase(customItem.getPriceUsd());
            allItemResponses.add(oldItemResponse);
        }

        // אם אין למשתמש בכלל הזמנות – מחזירים את הרשימה הריקה מכמויות
        if(orderResponses == null || orderResponses.isEmpty())
            return allItemResponses;

        // מחפשים אם יש הזמנה TEMP (עגלה פתוחה) מתוך כל ההזמנות
        for (OrderResponse orderResponse : orderResponses) {
            if (orderResponse.getOrderStatus() == OrderStatus.TEMP) {
                isTempExist = true;
                tempOrder = orderResponse;
                break;
            }
        }
//        if(orderResponses.getFirst().getOrderStatus() == OrderStatus.TEMP)
//            isTempExist = true;

        // אם אין עגלת TEMP – מחזירים את הרשימה הבסיסית (כמויות 0)
        if (!isTempExist || tempOrder == null) {
            return allItemResponses;
        }

        // לוקחים את הפריטים מתוך ההזמנה TEMP
        List<ItemResponse> newItemResponse = tempOrder.getItemResponses();

        // ממפים לפי itemId כדי שיהיה קל לעדכן את הרשימה הבסיסית
        Map<Long, ItemResponse> updatesById = newItemResponse.stream()
                .collect(Collectors.toMap(ItemResponse::getItemId, Function.identity()));

        // עוברים על כל הרשימה והיכן שיש התאמה ל-TEMP – מחליפים את הנתון המעודכן
        for (int i = 0; i < allItemResponses.size(); i++) {
            Long id = allItemResponses.get(i).getItemId();
            if (updatesById.containsKey(id)) {
                allItemResponses.set(i, updatesById.get(id));
            }
        }
        return allItemResponses;
    }

    // חיפוש פריטים לפי search + התאמת כמויות ומלאי לפי עגלת TEMP (בעיקר לדף הבית)
    public List<ItemResponse> getSearchedItems(String username, String page, String search) {

        // אם לא הגיע page – אין מה לעשות, מחזירים רשימה ריקה
        if (page == null || page.isEmpty())
            return new ArrayList<>();

        List<ItemResponse> itemResponses = new ArrayList<>();

        // מחפשים פריטים בטבלת items לפי הטקסט
        List<CustomItem> items = itemRepository.searchAtItems(search);

        // בודקים אם יש למשתמש עגלת TEMP פתוחה
        Long orderId = orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username);

        if (page.equals("home")) {

            // אין הזמנה TEMP → כל הכמויות = 0 והמלאי מלא
            if (orderId == null) {
                for (CustomItem item : items) {
                    ItemResponse response = buildItemResponse(item);
                    response.setMaxStock(item.getStock());
                    response.setQuantity(0);
                    response.setTotalPriceAtPurchase(0);
                    itemResponses.add(response);
                }
                return itemResponses;
            }

            // יש הזמנה TEMP – מביאים את הקשרים בין הזמנה לפריטים
            List<OrderItem> orderItems = orderItemService.getAllOrderItems(orderId);

            // בונים מפה של itemId → OrderItem כדי לחסוך חיפושים בלולאה
            Map<Long, OrderItem> orderItemsMap = orderItems.stream()
                    .collect(Collectors.toMap(OrderItem::getItemId, oi -> oi));

            for (CustomItem item : items) {

                ItemResponse response = buildItemResponse(item);

                // מחפשים אם הפריט הזה קיים בעגלת TEMP
                OrderItem match = orderItemsMap.get(item.getId());

                if (match == null) {
                    // הפריט לא בעגלה – כמות 0 והמלאי מלא
                    response.setMaxStock(item.getStock());
                    response.setQuantity(0);
                    response.setTotalPriceAtPurchase(0);
                } else {
                    // הפריט כן בעגלה – מעדכנים כמות, מלאי זמין וסכום כולל לפריט
                    response.setMaxStock(item.getStock() - match.getQuantity());
                    response.setQuantity(match.getQuantity());
                    response.setTotalPriceAtPurchase(match.getTotalPrice());
                }

                itemResponses.add(response);
            }
        }

        return itemResponses;
    }

    /// ///////// add item ////////////
    public int addItem(CustomItem item) {
        // בדיקה אם הנתונים תקינים
        if(item.getTitle() == null || item.getPriceUsd() < 0 || item.getStock() < 0) {
            return 0;
        }

        return itemRepository.addItem(item);
    }

    /// ///////// update item ////////////
    public int updateItem(CustomItem item) {
        // בדיקה אם הנתונים תקינים
        if(item.getId() < 0 || item.getTitle() == null || item.getPriceUsd() < 0 || item.getStock() < 0) {
            return 0;
        }

        // בדיקה אם הפריט קיים לפני עדכון
        CustomItem existing = itemRepository.getItemByIdHelper(item.getId());
        if(existing == null) {
            return 0;
        }

        return itemRepository.updateItem(item);
    }

    /// ///////// delete item ////////////
    public int deleteItem(Long itemId) {
        // בדיקה אם הפריט קיים לפני מחיקה
        CustomItem existing = itemRepository.getItemByIdHelper(itemId);
        if(existing == null || itemId == null) {
            return 0;
        }

        return itemRepository.deleteItem(itemId);
    }

    /// ////////////////   getHelpers ///////////////////////////////

    // בנאי קטן ל-ItemResponse מתוך CustomItem – בלי מידע על כמויות/מחירים
    private ItemResponse buildItemResponse(CustomItem item) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setItemId(item.getId());
        itemResponse.setPhotoUrl(item.getPhotoUrl());
        itemResponse.setTitle(item.getTitle());
        return itemResponse;
    }

    // מחזיר מחיר פריט לפי ID דרך הריפוזיטורי (שימוש בפונקציה קיימת)
    public Float getPriceById(Long itemId){

        return itemRepository.findPriceById(itemId);
    }

}