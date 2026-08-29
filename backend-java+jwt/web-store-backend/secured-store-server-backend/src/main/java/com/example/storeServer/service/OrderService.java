package com.example.storeServer.service;

import com.example.storeServer.dto.ItemResponse;
import com.example.storeServer.dto.OrderItemRequest;
import com.example.storeServer.dto.OrderResponse;
import com.example.storeServer.model.*;
import com.example.storeServer.repository.ItemRepository;
import com.example.storeServer.repository.OrderItemRepository;
import com.example.storeServer.repository.OrderRepository;
import com.example.storeServer.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // מביא הזמנה אחת לפי username + id (בעיקר לשימוש בפרונט לפתיחת מודאל/פרטים)
    public Order getOrderById(String username, Long id){

        return orderRepository.findOrderById(username, id);
    }

    // מחזיר את כל ההזמנות של משתמש (TEMP + CLOSE) בפורמט OrderResponse לפרונט
    public List<OrderResponse> getAllOrders(String username) {

        List<OrderResponse> orderResponses = new ArrayList<>();
        List<Order> orders = orderRepository.findAllOrders(username);
        if(orders == null || orders.isEmpty())
            return orderResponses;

        for (Order order : orders) {

            float totalPriceTemp = 0;
            float totalPriceClose = 0;
            if (order.getStatus() == OrderStatus.CLOSE) {
                totalPriceClose = order.getTotalPrice();
            }

            List<ItemResponse> itemResponses = new ArrayList<>();
            // כל הפריטים השייכים להזמנה הזו
            List<OrderItem> orderItems = orderItemRepository.findAllItemsByOrderId(order.getId()); // if we are here thats mean there is order for sure then must be one item at least

            for (OrderItem orderItem : orderItems) {

                CustomItem item = itemRepository.getItemByIdHelper(orderItem.getItemId());

                ItemResponse itemResponse = new ItemResponse();
                itemResponse.setPhotoUrl(item.getPhotoUrl());
                itemResponse.setTitle(item.getTitle());
                itemResponse.setQuantity(orderItem.getQuantity());
                itemResponse.setItemId(orderItem.getItemId());

                // חישוב maxStock תלוי במצב ההזמנה TEMP (כמה נשאר זמין במלאי)
                if (order.getStatus() == OrderStatus.TEMP){
                    if (orderRepository.isOrderWithStatusTempExistHelper(username))
                        itemResponse.setMaxStock(item.getStock() - orderItem.getQuantity());
                    else
                        itemResponse.setMaxStock(item.getStock());
                }

                // חישוב סכום זמני להזמנות TEMP וסכום קבוע להזמנות CLOSE
                if (order.getStatus() == OrderStatus.TEMP) {
                    totalPriceTemp += orderItem.getTotalPrice();
                    itemResponse.setTotalPriceAtPurchase(orderItem.getTotalPrice());
                } else {
                    itemResponse.setTotalPriceAtPurchase(orderItem.getTotalPrice());
                }

                itemResponses.add(itemResponse);
            }

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setOrderStatus(order.getStatus());
            orderResponse.setItemResponses(itemResponses);
            if(order.getStatus().name().equals("TEMP")) {
                orderResponse.setTotalPrice(totalPriceTemp);
            }
            else {
                orderResponse.setTotalPrice(totalPriceClose);
            }

            orderResponses.add(orderResponse);
        }

        // הופכים את הסדר כדי שההזמנות האחרונות יופיעו למעלה
        Collections.reverse(orderResponses);

        return orderResponses;
    }

    // יצירת הזמנה TEMP חדשה למשתמש (אם כבר קיימת כזו – מחזירים את ה-id שלה)
    public Long createOrder(String username){
        if (orderRepository.isOrderWithStatusTempExistHelper(username)) {
            return orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username); // if that return null thats mean internal error
        }

        CustomUser user = userRepository.findUserByUsername(username); // if we are here thats mean there is a user for sure
        int rows = orderRepository.saveOrder(user);
        if(rows > 0)
            return orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username);

        return (long) -1;
    }

    // קניית הזמנה TEMP → סוגר אותה כ-CLOSE + מעדכן מלאים + מעדכן עגלות TEMP אחרות
    @Transactional
    public Order buyOrder(String username){
        if (!orderRepository.isOrderWithStatusTempExistHelper(username))
            return null;

        Long userOrderId = orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username);
        if(userOrderId == null)
            return null;

        // סכום כולל להזמנה לפי כל הפריטים שלה
        float totalPrice = orderItemRepository.getTotalPriceHelper(userOrderId); // there is order id then there is item then there is total price
        Order order = orderRepository.update(username, userOrderId, totalPrice);

        // עדכון מלאי בפועל לפי ההזמנה שנקנתה
        List<Integer> updatedStocks = updateItemStocksHelper(userOrderId);
        if(updatedStocks == null) return null;

        // מביאים את כל ההזמנות TEMP במערכת – כדי לסנכרן כמויות לפי המלאי החדש
        List<Long> allTempOrders = orderRepository.getAllOrdersIdsTemp();

        for(Long orderIdTemp: allTempOrders) {
            List<Long> itemIds = orderItemRepository.getListOfItemsIdsHelper(orderIdTemp);
            List<Integer> oldQuantities = orderItemRepository.getListOfQuantitiesHelper(orderIdTemp);
            List<Float> unitsPrices = new ArrayList<>();
            int i = 0;
            int rows;

            for (Long itemId : itemIds) {
                // מחיר ליחידה עבור הפריט בהזמנה הזו
                unitsPrices.add(orderItemRepository.getUnitPriceHelper(orderIdTemp, itemId));
                Integer availableStock = itemRepository.getStockByItemIdHelper(itemId);
                if(availableStock == null)
                    return null;

                // כמות חדשה = המינימום בין מה שהיה בעגלה לבין המלאי הזמין
                int newQuantity = Math.min(oldQuantities.get(i), availableStock);
                float newTotalPrice = unitsPrices.get(i) * newQuantity;

                // עדכון כמות חדשה וסכום חדש להזמנה TEMP
                rows = orderItemRepository.updateNewQuantitiesAndNewTotalPricesHelper(orderIdTemp, itemId, newQuantity, newTotalPrice);
                if(rows <= 0) return null;

                // אם הכמות הפכה ל-0 – מוחקים את הפריט מההזמנה
                if(newQuantity == 0){
                    rows = orderItemRepository.deleteOrderItem(orderIdTemp, itemId);
                    if(rows <= 0) return null;
                }

                i++;
            }

            // אם אין יותר פריטים בהזמנה TEMP – מוחקים את ההזמנה עצמה
            List<OrderItem> remainingItems = orderItemRepository.findAllItemsByOrderId(orderIdTemp);
            if(remainingItems.isEmpty()){
                rows = orderRepository.deleteOrder(orderIdTemp);
                if(rows <= 0) return null;
            }
        }

        return order;
    }

    // מחיקת הזמנת TEMP של המשתמש (למשל "ריקון עגלה")
    public int deleteOrder(String username) {
        if (!orderRepository.isOrderWithStatusTempExistHelper(username))
            return 0;

        Long orderId = orderRepository.FindOrderIdWithStatusTempByUsernameHelper(username);
        return orderRepository.deleteOrder(orderId);
    }

    /// ////////////// helpers ///////////////////////

    // עדכון מלאי הפריטים לפי ההזמנה – מוריד מהמלאי את הכמות שנקנתה
    private List<Integer> updateItemStocksHelper(Long orderId){
        List<Integer> quantities = orderItemRepository.getListOfQuantitiesHelper(orderId);
        List<Long> itemsIds = orderItemRepository.getListOfItemsIdsHelper(orderId);
        List<Integer> updatedStocks = new ArrayList<>();
        int rows;

        for (int i = 0; i < itemsIds.size(); i++) {
            Long itemId = itemsIds.get(i);
            int quantity = quantities.get(i);

            int currentStock = itemRepository.getStockByItemIdHelper(itemId);

            int newStock = Math.max(currentStock - quantity, 0);

            rows = itemRepository.updateItemStocksHelper(itemId, newStock);
            if (rows <= 0) {
                return null;
            }

            updatedStocks.add(newStock);
        }

        return updatedStocks;
    }

}