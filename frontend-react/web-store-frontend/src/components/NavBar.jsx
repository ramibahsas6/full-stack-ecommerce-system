import React, { useContext, useEffect, useRef, useState} from "react";
import { NavLink, useNavigate , useLocation} from "react-router-dom";
import UserContext from "../contexts/UserContext";
import ShoppingBagTwoToneIcon from '@mui/icons-material/ShoppingBagTwoTone';
import "../styles/NavBar.css";
import { deleteUserRequest, getAllOrders } from "../services/ApiServices";
import LocalGroceryStoreRoundedIcon from '@mui/icons-material/LocalGroceryStoreRounded';


export default function Navbar({ onSearch, setOnSearch }) {
  // הקשר גלובלי של המשתמש המחובר
  const { user, setUser } = useContext(UserContext);
  const navigate = useNavigate();
   const location = useLocation();
  const isHome = location.pathname === "/";
  const searchInputRef = useRef(null);
  const [cartCount, setCartCount] = useState(0); // כמות הפריטים בעגלה (TEMP order)

  // פוקוס על שדה החיפוש כאשר המשתמש מחובר ונמצא בדף הבית
  useEffect (() => {
    if(user && isHome)
    searchInputRef.current?.focus();
  },[isHome, user]);

  // ***************  NEW: cartCount effect  ***************
  useEffect(() => {
    // פונקציה שמביאה את כל ההזמנות ומחשבת את כמות הפריטים בהזמנת TEMP (עגלה)
    const fetchCartCount = async () => {
      try {
        if (!user) {
          setCartCount(0);
          return;
        }

        const { data } = await getAllOrders();

        // מחפשים את ההזמנה במצב TEMP – זו העגלה הנוכחית של המשתמש
        const tempOrder = data.find(o => o.order_status === "TEMP");

        if (!tempOrder || !tempOrder.items) {
          setCartCount(0);
          return;
        }

        // סכימת הכמויות של כל הפריטים בהזמנת TEMP
        const count = tempOrder.items.reduce((sum, item) => sum + item.quantity, 0);
        setCartCount(count);
      } catch (err) {
        console.log("Error fetching cart count:", err);
      }
    };

    // טעינה ראשונית של העגלה כאשר הקומפוננטה נטענת או שהמשתמש משתנה
    fetchCartCount();

    // מאזין גלובלי לאירוע "ordersUpdated" מהמודאל כדי לעדכן את ה-badge בראש הדף
    const handleOrdersUpdated = () => {
      fetchCartCount();
    };

    window.addEventListener("ordersUpdated", handleOrdersUpdated);

    // ניקוי המאזין כאשר הקומפוננטה מתפרקת
    return () => {
      window.removeEventListener("ordersUpdated", handleOrdersUpdated);
    };
  }, [user]);
  // ***************  END NEW  ***************
  
  // התנתקות משתמש: איפוס חיפוש, מחיקת טוקן, ניקוי user מהקונטקסט וניווט לדף הבית
  const handleLogout = () => {
    setOnSearch("");
    localStorage.removeItem("token");
    setUser(null);
    navigate("/");
  };

  // פונקציה שמחזירה class שונה לפי האם ה-NavLink אקטיבי
  const getLinkClass = ({ isActive }) => isActive ? "nav-link active" : "nav-link";

  // מחיקת משתמש מהמערכת (כולל אישור מהמשתמש עצמו)
  const handleDeleteUser = async () => {
  const confirmText = prompt("To confirm deleting your account, type: ok");

  if (confirmText !== "ok") {
    alert("User deletion canceled.");
    return;
  }

  try {
    await deleteUserRequest();
    localStorage.removeItem("token");
    setUser(null);
    navigate("/", { state: { userDeleted: true } });
  } catch (err) {
    console.log(err);
    alert("Error deleting user.");
  }
};


  return (
    <nav className="navbar-container">
      <div className="navbar-left">
        {/* לוגו – אות ראשונה של שם המשתמש + שם המותג + אייקון תיק קניות */}
        <div className="logo">
          <div className="logo-icon-box">{user ? user.username.charAt(0).toUpperCase() : 'M'}</div>
          <span className="logo-text">MAVIX</span>
          <ShoppingBagTwoToneIcon />
        </div>
        <div className="nav-links">
          <NavLink to="/" className={getLinkClass}>Home</NavLink>
          {user && <NavLink to="/favorites" className={getLinkClass}>Favorites</NavLink>}
      {user && (
  <NavLink to="/orders" className={getLinkClass}>
    {/* כל האזור הזה (טקסט Orders + אייקון + badge) הוא קישור אחד לדף ההזמנות */}
    <div className="orders-container">
      <span>Orders</span>

      <div className="orders-cart-wrapper">
        <LocalGroceryStoreRoundedIcon className="orders-cart-icon" />

        {cartCount > 0 && (
          <span className="orders-badge">{cartCount}</span>
        )}
      </div>
    </div>
  </NavLink>
)}
      {user && user.role === "ADMIN" && <NavLink to="/admin" className={getLinkClass}>Admin</NavLink>}
        </div>
      </div>

      {user && isHome && (
        <div className="navbar-center">
          {/* תיבת חיפוש – נשלטת מה-Navbar ומעבירה ערך כלפי מעלה דרך props */}
          <div className="search-box">
            <input className="search-input" ref={searchInputRef} type="text" placeholder="Search..." value={onSearch} onChange={(e) => setOnSearch(e.target.value)}/>
            <button className="search-btn">Go</button>
          </div>
        </div>
      )}

      <div className="navbar-right">
        {user ? (
          <>
  {/* כפתור למחיקת חשבון */}
  <button className="delete-button" onClick={handleDeleteUser}>Delete Account</button>
            {/* הצגת שם המשתמש המחובר בצד ימין למעלה */}
            <span className="welcome-user" style={{marginRight: "10px" , color: "#E8E8FF",
    fontWeight: 600,
    letterSpacing: "5px",
    textShadow:
      "0 0 6px rgba(0, 0, 0, 0)", fontFamily: "JetBrains Mono, monospace", fontStyle: "italic", fontSize: "1rem"}}>{user.username}</span>
            {/* כפתור Logout לניתוק המשתמש */}
            <button className="logout-btn" onClick={handleLogout} style={{"fontFamily": 'JetBrains Mono, monospace', fontSize: "1rem", color: "black"}}>Logout</button>
          </>
        ) : (
           <>
           <NavLink to="/register" className={getLinkClass} ><span style={{"fontFamily": "JetBrains Mono, monospace", 
            background: "gold",border: "none", borderRadius: "6px", padding: "6px 12px", cursor: "pointer", fontWeight: "bold", color: "black"}}>
              Sign Up</span></NavLink>
          <NavLink to="/login" className={getLinkClass}><span style={{"fontFamily": "JetBrains Mono, monospace", 
            background: "gold",border: "none", borderRadius: "6px", padding: "6px 12px", cursor: "pointer", fontWeight: "bold", color: "black"}}>
          Login</span></NavLink>
        </>)}
      </div>
    </nav>
  );
}