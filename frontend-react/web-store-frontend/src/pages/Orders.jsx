import React, { useEffect, useState } from 'react'
import '../styles/Orders.css'
import OrderCard from '../components/OrderCard';
import OrderItemsModal from '../components/OrderItemsModal';
import { getAllOrders } from '../services/ApiServices';

// קומפוננטה הראשית שמציגה את כל ההזמנות של המשתמש
const Orders =() => {

  // סטייט של כל ההזמנות מהשרת
  const [orders, setOrders] = useState([]);

  // ההזמנה שנבחרה לצפייה במודל
  const [selectedOrder, setSelectedOrder] = useState(null);

  // טעינה ראשונית – עד להבאת המידע מהשרת
  const [loading, setLoading] = useState(true);

  // אפקט שמביא את כל ההזמנות מהשרת ברגע שהעמוד נטען
  useEffect(() => {
    async function loadOrders() {
      try {
        // בקשה לשרת לקבלת כל ההזמנות של המשתמש
        const {data} = await getAllOrders();
        setOrders(data);
      } catch (error) {
        console.error("Error loading orders:", error);
      } finally {
        // אחרי שהמידע הגיע (או הייתה שגיאה) – מפסיקים טעינה
        setLoading(false);
      }
    }

    loadOrders();
  }, []);

  // בזמן טעינה – הצגת מסך זמני
  if (loading) {
    return (
      <div className='pageStyle'>
        <h1 className='titleStyle'>My Orders</h1>
        <div>Loading orders…</div>
      </div>
    );
  }

  return (
    <>
    {/* אם יש הזמנות */}
    {orders.length > 0 && <div className='pageStyle'>
      <h1 className='titleStyle' style={{fontFamily: "'Playfair Display', serif"}}>My Orders</h1>

      {/* גריד של כל כרטיסי ההזמנות */}
      <div className='ordersGridStyle'>
        {orders.map((order) => (
          <OrderCard
            key={order.order_id}                 // מפתח ייחודי לרינדור
            order={order}                        // מעביר את אובייקט ההזמנה
            onView={() => setSelectedOrder(order)}  // פתיחת המודל לצפייה בפריטים
          />
        ))}
      </div>

      {/* מודל הצגת פריטי ההזמנה – מוצג רק אם נבחרה הזמנה */}
      {selectedOrder && (
        <OrderItemsModal
          order={selectedOrder}
          onClose={() => setSelectedOrder(null)}
          setOrders={setOrders}                // מאפשר עדכון רשימת הזמנות
          setSelectedOrder={setSelectedOrder}  // סגירה/עדכון המודל
        />
      )}
    </div>}

    {/* אם אין בכלל הזמנות */}
    { orders.length === 0 && <>
      <div className='pageStyle'>
        <h1 className='titleStyle'>No orders found</h1>
      </div>
    </>}
    </>
  );
}

export default Orders