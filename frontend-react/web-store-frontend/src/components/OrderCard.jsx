import React, { useEffect, useState } from 'react'
import '../styles/OrderCard.css'
import { getUserOrder } from '../services/ApiServices';
import { ReactComponent as CartIcon } from '../assets/icons/cart.svg';

// קומפוננטת כרטיס הזמנה ב"דף ההזמנות"
// מציגה גם הזמנה סגורה (CLOSE) וגם הזמנת TEMP עם עגלה
const OrderCard = ({ order, onView }) => {
  const [orderOtherDetails, setOrderOtherDetails] = useState(null); 
  // נשמור כאן פרטים נוספים על ההזמנה (תאריך, כתובת...) שמגיעים מקריאת API נוספת

  // פונקציה שמביאה הזמנה מלאה לפי order_id מהשרת
  const getOrderById = async () => {
    try {
      const { data } = await getUserOrder(order.order_id);
      setOrderOtherDetails(data);   // שומרים סטייט עם הפרטים הנוספים
    } catch (error) {
      console.error("Error fetching order details:", error);
    }
  };

  // נטען את פרטי ההזמנה הנוספים רק פעם אחת כשהכרטיס נטען
  useEffect(() => {
    getOrderById();
  },[]);

  return (
    <>
    {/* כרטיס להזמנה סגורה (אחרי תשלום) */}
    {order.order_status === "CLOSE" && <div className='cardStyle'>
      <div>
        <div className='orderIdStyle'>Order #{order.order_id}</div>
        <div className='metaStyle'>Status : • <span style={{color: "white"}}>{order.order_status}</span></div>
       {orderOtherDetails && (
  <>
    <div className='metaStyle'>
      Date: {orderOtherDetails.order_date}
    </div>

    <div className='metaStyle'>
      Address: {orderOtherDetails.shipping_country}, {orderOtherDetails.shipping_city}
    </div>
  </>
)}
        <div className='totalStyle'>Total: {order.total_price} USD</div>
      </div>
      <div> Payment completed </div>
    <button className='viewButtonStyle' style={{minWidth: "10%" }} onClick={onView}>
        View order
      </button>
    </div>}

    {/* כרטיס להזמנת TEMP – מייצגת "סל פעיל" עם אייקון עגלה */}
    {order.order_status === "TEMP" && <div>
      <div className='cardStyleTemp'>
        <div>
          <CartIcon className="cart-icon" />
          <br /><br />
          <div className='orderIdStyle'>Order #{order.order_id}</div>
          <div className='metaStyle'>Status : • <span style={{color: "red"}}>{order.order_status}</span></div>
          {orderOtherDetails && (
  <>
    <div className='metaStyle'>
      <h4 style={{padding: "0 0 0 0", margin: "0 0 0 0", letterSpacing: "1.1px", lineHeight: "1.3"}}>Date: {orderOtherDetails.order_date}</h4>
    </div>

    <div className='metaStyle'>
      Address: {orderOtherDetails.shipping_country}, {orderOtherDetails.shipping_city}
    </div>
  </>
)}
          <div className='totalStyle'>Total: {order.total_price} USD</div>
        </div>
        <br />
        <button className='viewButtonStyle' style={{ color: "gold", fontSize: "13px", minWidth: "15%" , fontFamily: "'Merriweather', serif", letterSpacing: "1.1px" , textTransform: "uppercase"}} onClick={onView}>
        View order Temp
      </button>
      </div>
    </div>}
    </>
  )
}

export default OrderCard