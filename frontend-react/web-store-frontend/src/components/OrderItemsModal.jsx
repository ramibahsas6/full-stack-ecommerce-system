import React, { useEffect, useState } from 'react'
import '../styles/OrderItemsModal.css'
import { addItem, buyOrder, getAllOrders, getPriceByItemId, removeItem } from '../services/ApiServices';
import AddShoppingCartOutlinedIcon from '@mui/icons-material/AddShoppingCartOutlined';
import RemoveShoppingCartOutlinedIcon from '@mui/icons-material/RemoveShoppingCartOutlined';

// מודאל שמציג את כל הפריטים בתוך הזמנה ספציפית,
// כולל אפשרות להוסיף/להוריד פריטים בהזמנת TEMP ולבצע BUY להזמנה.
const OrderItemsModal = ({ order, onClose, setOrders , setSelectedOrder}) => {
  
  // ה־items שאנחנו מציגים בתוך המודאל
  const [items, setItems] = useState(order.items || []);

  // שמירת מחיר עדכני לכל פריט לפי item_id (משרת)
  const [itemsPrices, setItemsPrices] = useState({});

  // מניעת הוספה אם אין מלאי
  const [disabledAdd, setDisabledAdd] = useState({});

  // פעולה לסגירת ההזמנה (BUY)
  const handleBuy = async() => {
    try {
      const {data} = await buyOrder();
      if(data){
        alert("Order bought successfully!, You will be redirected to your orders page.");
        window.dispatchEvent(new Event('ordersUpdated'));
        window.location.reload();
      }   
    }catch (error) {
      console.error("Error buying items:", error);
    }
  };

  // טעינת מחירים עדכניים לכל פריט
  useEffect(() => {
    items.forEach(async (item) => {
      try {
        const { data } = await getPriceByItemId(item.item_id);
        setItemsPrices(price => ({ ...price, [item.item_id]: data }));
      } catch (err) {
        console.log(err);
      }
    });
  }, [items]);

  // מחיקת פריט אחד מההזמנה
  const handleDelete = async (item) => {
    setDisabledAdd(d => ({ ...d, [item.item_id]: false }));

    // אם זה הפריט האחרון — נסגור את המודאל
    if (item.quantity === 1 && items.length === 1) {
      try { await removeItem(item, false); } catch (err) { console.log(err); }
      
      window.dispatchEvent(new Event('ordersUpdated'));
      onClose();
      window.location.reload();
      return;
    }

    try { await removeItem(item, false); } catch (err) { console.log(err); }

    // טוענים מחדש את ההזמנות מהשרת כדי לעדכן את המודאל
    try {
      const { data } = await getAllOrders();
      setOrders(data);

      const updatedOrder = data.find(o => o.order_id === order.order_id);
      if (updatedOrder) {
        setSelectedOrder(updatedOrder);
        setItems(updatedOrder.items || []);
      }

      window.dispatchEvent(new Event('ordersUpdated'));
    } catch (err) {
      console.log(err);
    }
  };

  // הוספת כמות לפריט בהזמנה
  const handleAdd = async (item) => {
    setDisabledAdd(d => ({ ...d, [item.item_id]: false }));

    // אין מלאי → לא להוסיף
    if (item.max_stock === 0){ 
      setDisabledAdd(d => ({ ...d, [item.item_id]: true }));
      return;
    }

    try { await addItem(item, true); } catch (err) { console.log(err); }

    try {
      const { data } = await getAllOrders();
      setOrders(data);

      const updatedOrder = data.find(o => o.order_id === order.order_id);
      if (updatedOrder) {
        setSelectedOrder(updatedOrder);
        setItems(updatedOrder.items || []);
      }

      window.dispatchEvent(new Event('ordersUpdated'));
    } catch (err) {
      console.log(err);
    }
  };

  // סגירת המודאל
  const closeWindow = () => {
    onClose();
    window.location.reload();
  };

  return (
    <div className='backdropStyle' onClick={closeWindow}>
      <div
        className='modalStyle'
        onClick={(e) => e.stopPropagation()}
      >
        
        {/* כותרת המודאל */}
        <div className='headerStyle'>
          <div>
            <div style={{ fontWeight: 600, fontSize: "24px" }}>
              Order #{order.order_id}
            </div>
            <div style={{ fontSize: "14px", color: "#B0B0B0", marginTop: "4px" }}>
              Status : • {order.order_status}
            </div>
          </div>
          <button className='closeButtonStyle' onClick={closeWindow}>✕</button>
        </div>

        {/* רשימת הפריטים */}
        <div style={{ borderRadius: "4px",fontSize: "18px", fontWeight: 600, marginBottom: "8px" , backgroundColor: "#d8c071ff", padding: "8px", fontFamily: "sans-serif", color: "#111111"}}>
          Items in this order
        </div>

        <div className='itemsListStyle'>
          {items.map((item) => (
            <div key={item.item_id} className='itemRowStyle'>
              <div>
                <img
                  style={{ width: "130px", height: "130px" }}
                  src={item.photo_url}
                  alt={item.title}
                  className='itemImageStyle'
                />

                <div style={{ fontSize: "16px" }}>{item.title}</div>

                {/* כפתורי הוספה/הורדה רק בהזמנת TEMP */}
                {order.order_status === "TEMP" && (
                  <button style={{border: "none" , background: "none"}} onClick={() => handleDelete(item)}>
                    <RemoveShoppingCartOutlinedIcon style={{color:"#f9f5f5ff", fontSize: "1.8rem"}}/>
                  </button>
                )}

                <span style={{ fontSize: "14px", color: "#A0A0A0" }}>
                  Quantity: {item.quantity} • Price : {itemsPrices[item.item_id]} USD
                </span> 

                {order.order_status === "TEMP" && (
                  <button style={{border: "none" , background: "none"}} onClick={() => handleAdd(item)}>
                    <AddShoppingCartOutlinedIcon  
                      style={{fontSize: "1.8rem" , color: `${ disabledAdd[item.item_id] ? 'red' : "#f9f5f5ff"}`}}
                    />
                  </button>
                )}
                
                <div style={{ fontSize: "15px", color: "#A0A0A0" }}>
                  Total for this item: {item.price_usd}
                </div>

                {order.order_status === "TEMP" && (
                  <div>Max in stock: {item.max_stock}</div>
                )}
              </div>
            </div>
          ))}
        </div>

        {/* סה״כ להזמנה */}
        <div className='footerStyle'>
          <div style={{fontSize: "16px", fontWeight: 600 , color: "#ff0000ff", marginTop: "12px"}}>
            Total Price: {order.total_price} USD
          </div>
        </div>

        {/* כפתור BUY מופיע רק אם ההזמנה TEMP */}
        {order.order_status === "TEMP" && (
          <div className='footerStyle'>
            <button className='buyAgainButtonStyle' onClick={handleBuy}>
              Buy this Order
            </button>
          </div>
        )}

      </div>
    </div>
  );
}

export default OrderItemsModal