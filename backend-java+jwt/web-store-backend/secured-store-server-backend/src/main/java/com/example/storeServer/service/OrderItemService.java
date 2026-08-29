package com.example.storeServer.service;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.dto.OrderItemRequest;
import com.example.storeServer.model.CustomItem;
import com.example.storeServer.model.OrderItem;
import com.example.storeServer.repository.ItemRepository;
import com.example.storeServer.repository.OrderItemRepository;
import com.example.storeServer.repository.OrderRepository;
import com.example.storeServer.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // מחזיר פריט יחיד מתוך order_items לפי הבקשה
    public OrderItem getOrderItem(OrderItemRequest orderItemRequest){
        return orderItemRepository.findOrderItem(orderItemRequest);
    }

    // מחזיר את כל הפריטים השייכים להזמנה מסוימת
    public List<OrderItem> getAllOrderItems(Long orderId)
    {
        return orderItemRepository.findAllItemsByOrderId(orderId);
    }

    // לוגיקה מרכזית לעדכון/יצירה של פריט בעגלה (TEMP) לפי פעולה מהפרונט
    @Transactional
    public ItemResponse changeOrderItem(String username, ItemResponse itemResponse, boolean isFromCreate){
        CustomItem item = itemRepository.getItemByIdHelper(itemResponse.getItemId());
        if(item == null)
            return null;
        if(item.getStock() == 0)
            return null;
        if(itemResponse.getQuantity() == 0 && !isFromCreate) {
            return null;
        }
        Long orderId = 0L;
        if(isFromCreate) {
            // יצירת הזמנה TEMP חדשה במידת הצורך
            orderId = orderService.createOrder(username);
        }
        else{
            // שימוש בהזמנת TEMP קיימת אם יש
            if(orderRepository.isOrderWithStatusTempExistHelper(username)) {
                orderId = orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username);
            }else
                orderId = -1L;
        }
        if(orderId == -1) // order temp exist / בעיה בלוגיקה
            return null;

        // אם אין עדיין רשומת OrderItem לפריט הזה – יוצרים חדשה
        if(orderItemRepository.isUniqueOrderItemHelper(orderId, item.getId()).isEmpty()) {
            if(!isFromCreate)
                return null;
            OrderItem newOrderItem = createOrderItemHelper(orderId, itemResponse, item);
            if(newOrderItem == null)
                return null;
            itemResponse.setItemId(newOrderItem.getItemId());
            itemResponse.setTotalPriceAtPurchase(newOrderItem.getTotalPrice());
            itemResponse.setQuantity(newOrderItem.getQuantity());
            itemResponse.setTitle(item.getTitle());
            itemResponse.setPhotoUrl(item.getPhotoUrl());
            itemResponse.setMaxStock(item.getStock()-newOrderItem.getQuantity());
            return itemResponse;
        }

        // כאן מעדכנים פריט קיים (כמות, מחיר וכו')
        OrderItem orderItemForUpdate = new OrderItem();
        orderItemForUpdate.setOrderId(orderId);
        orderItemForUpdate.setItemId(item.getId());
        orderItemForUpdate.setQuantity(itemResponse.getQuantity());
        orderItemForUpdate.setPriceAtPurchase(item.getPriceUsd());
        orderItemForUpdate.setTotalPrice(itemResponse.getTotalPriceAtPurchase());
        OrderItem newOrderItem = updateOrderItem(username, orderItemForUpdate, isFromCreate);
        if(newOrderItem == null)
            return null;

        // בניית ItemResponse מעודכן לפרונט
        itemResponse.setItemId(newOrderItem.getItemId());
        itemResponse.setTotalPriceAtPurchase(newOrderItem.getTotalPrice());
        itemResponse.setQuantity(newOrderItem.getQuantity());
        itemResponse.setTitle(item.getTitle());
        itemResponse.setPhotoUrl(item.getPhotoUrl());
        itemResponse.setMaxStock(item.getStock()-newOrderItem.getQuantity());
        return itemResponse;
    }

    /// /////////////// helpers //////////////////////////////////

    @Transactional
    public OrderItem updateOrderItem(String username ,OrderItem orderItem, boolean isFromCreate){
        // ולידציה בסיסית על ערכים לא חוקיים
        if(
                orderItem.getOrderId() <= 0 || orderItem.getItemId() <= 0 ||
                        orderItem.getQuantity() < 0 || orderItem.getPriceAtPurchase() < 0 || orderItem.getTotalPrice() < 0
        ) // for not null fields
            return null;

        // מביא את הכמות הנוכחית מהדאטה־בייס, לפני שינוי
        orderItem.setQuantity(orderItemRepository.getQuantityHelper(orderItem));
        Integer currentStock = itemRepository.getStockByItemIdHelper(orderItem.getItemId());
        if(currentStock == null)
            return null;

        // הורדה מהעגלה (לא יצירה)
        if(!isFromCreate)
        {
            if(orderItem.getQuantity() <= 1)
            {
                // אם נשאר 1 → מוחקים את הפריט מההזמנה
                orderItem.setQuantity(0);
                orderItem.setTotalPrice(0);
                Long orderId = orderItem.getOrderId();
                orderItemRepository.update(orderItem);
                orderItemRepository.deleteOrderItem(orderItem.getOrderId(), orderItem.getItemId());
                if(orderItemRepository.findAllItemsByOrderId(orderId).isEmpty())
                    orderService.deleteOrder(username);

                return new OrderItem(0,0,0);

            }

            // במקרה הרגיל – מורידים כמות ב־1
            orderItem.setQuantity(orderItem.getQuantity() - 1);
            return orderItemRepository.update(orderItem);
        }

        // במקרה של הוספה (create / increment) – מוסיפים 1 אם יש מלאי
        if(orderItem.getQuantity() < currentStock)
            orderItem.setQuantity(orderItem.getQuantity() + 1);

        return orderItemRepository.update(orderItem);
    }

    @Transactional
    private OrderItem createOrderItemHelper(Long orderId,ItemResponse itemResponse, CustomItem item){
        // יצירת רשומת OrderItem חדשה עם quantity = 1
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setItemId(item.getId());
        orderItem.setQuantity(1);
        orderItem.setPriceAtPurchase(item.getPriceUsd());
        orderItem.setTotalPrice(item.getPriceUsd());
        return orderItemRepository.create(orderItem);
    }

    /// ///////// delete order item ////////////
    public int deleteOrderItem(Long orderId, Long itemId) {
        // בדיקה אם הנתונים null – מניעת טעויות
        if(orderId == null || itemId == null) {
            return 0;
        }

        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setOrderId(orderId);
        orderItemRequest.setItemId(itemId);

        // בדיקה אם הפריט קיים קודם – אופציונלי אך מומלץ
        OrderItem existing = orderItemRepository.findOrderItem(orderItemRequest);
        if(existing == null) {
            // אם לא קיים – לא ניתן למחוק
            return 0;
        }

        // קריאה ל-repository למחיקה
        return orderItemRepository.deleteOrderItem(orderId, itemId);
    }

}