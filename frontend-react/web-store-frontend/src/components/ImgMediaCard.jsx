import React, { useContext, useEffect, useState } from 'react';
import "../styles/ImgMediaCard.css";
import Card from '@mui/material/Card';
import CardMedia from '@mui/material/CardMedia';
import FavoriteBorderOutlinedIcon from '@mui/icons-material/FavoriteBorderOutlined';
import FavoriteOutlinedIcon from '@mui/icons-material/FavoriteOutlined';
import UserContext from '../contexts/UserContext';
import { addItem, removeItem, addToFavorites, removeFromFavorites, getPriceByItemId } from '../services/ApiServices';
import AddShoppingCartOutlinedIcon from '@mui/icons-material/AddShoppingCartOutlined';
import RemoveShoppingCartOutlinedIcon from '@mui/icons-material/RemoveShoppingCartOutlined';

// קומפוננטה של כרטיס מוצר – מציגה תמונה, מחיר, כמות, סטוק ופייבוריט
export default function ImgMediaCard({ item, ifFavorite, priceUsd, refresh, isCurrentFavorite, onFavoriteRemoved }) {
  const { user } = useContext(UserContext);
  const [isFavorite, setIsFavorite] = useState(false);      // האם הפריט מסומן בפייבוריט בצד הקליינט
  const [price, setPrice] = useState(priceUsd);             // מחיר שמוצג לפי מצב (פייבוריט / רגיל)
  const [showAlert, setShowAlert] = useState(false);        // פלג להצגת הודעת שגיאה קטנה מתחת לכפתורים
  const [alertMessage, setAlertMessage] = useState('');     // טקסט ההודעה למשתמש
  

  // useEffect שמטען מחיר מהשרת אם אנחנו במסך פייבוריטס
  useEffect(() => {
    const fetchPrice = async () => {
      if (ifFavorite) {
        try {
          const { data } = await getPriceByItemId(item.item_id);
          setPrice(data); // מחיר חי מהשרת לפי item_id
        } catch (err) {
          console.log(err);
        }
      } else {
        setPrice(priceUsd); // במסכים אחרים נשתמש במחיר שהגיע מהורה
      }
    };

    fetchPrice();
  }, [ifFavorite, item?.item_id, priceUsd]);

  // מסנכרן את ה־isFavorite לפי מה שמגיע מהורה כשהמשתמש או הרשימה מתעדכנים
  useEffect(() => {
    if (!user) return;
    setIsFavorite(isCurrentFavorite);
  }, [isCurrentFavorite, user, item?.item_id]);

  // הורדת כמות מהסל – אם הכמות 0 מציגים הודעה ולא קוראים לשרת
  const handleDelete = async () => {
    setShowAlert(false);
    if (item.quantity === 0){ 
      setAlertMessage("0 items left in stock");
      setShowAlert(true);
      return;
    }
    await removeItem(item, false);                    // קריאה לשרת להוריד כמות
    window.dispatchEvent(new Event('ordersUpdated')); // מעדכן את הבאדג' של העגלה בניווט
    refresh();                                        // ריענון רשימת הפריטים מהשרת
  };

  // הוספת כמות לסל – אם אין סטוק מציגים הודעה
  const handleAdd = async () => {
    setShowAlert(false);
    if (item.max_stock === 0){
      setAlertMessage("can't add quantity more than stock");
      setShowAlert(true);
      return;
    }
    await addItem(item, true);                        // קריאה לשרת להוסיף כמות/פריט
    window.dispatchEvent(new Event('ordersUpdated')); // טריגר לעדכון הספירה בניווט
    refresh();                                        // ריענון רשימת הפריטים מהשרת
  };

  // הוספה / מחיקה מפייבוריטס בצד השרת
  const handleAddToFavorites = async (newIsFavorite) => {
    try {
      if (newIsFavorite) 
        await addToFavorites(item);
      else 
        await removeFromFavorites(item);
    } catch (err) { console.log(err); }
  };

  // לחיצה על אייקון הלב – משנה סטייט מקומי ושולח עדכון לשרת
  const toggleIsFavorite = () => {
    const newIsFavorite = !isFavorite;
    setIsFavorite(newIsFavorite);
    handleAddToFavorites(newIsFavorite);
    if (!newIsFavorite && onFavoriteRemoved) onFavoriteRemoved(item.item_id); // מעדכן את רשימת הפייבוריטס אצל ההורה
  };

  return (
    <Card sx={{ maxWidth: 350 }} style={{ border: "2px solid gold", color: "black"}}>
      <CardMedia component="img" alt={item.title} height="214" image={item.photo_url} />
      <h2 style={{ color: "#C9A227", textAlign: "center", fontSize: "1.4rem" }}>{item.title}</h2>

      {user && (
        <>
          {/* אזור כמות, מחיר, פייבוריטס ושגיאות – למשתמש מחובר בלבד */}
          <h3 style={{ color: "#111111"}}>Price : <span style={{ fontSize: "2rem" }}>{ifFavorite ? price : priceUsd}</span> <span style={{ fontSize: "1.1rem", color: "#4B4B4B" }}>USD</span></h3>
          <div style={{  textAlign: "center" , color: "black"}}>
            <button style={{border: "none" , background: "none"}} onClick={handleDelete}><RemoveShoppingCartOutlinedIcon style={{color:"#2D2D2D", fontSize: "1.8rem"}}/></button>
            <span style={{ margin: "0 20px", fontSize: "1.5rem", fontWeight: "bold" }}>{item.quantity}</span>
            <button style={{border: "none" , background: "none"}} onClick={handleAdd}><AddShoppingCartOutlinedIcon style={{color:"#2D2D2D", fontSize: "1.8rem" }}/></button>
            {showAlert && <div> <span style={{color: "red"}}>{alertMessage}</span></div>}
            {!showAlert && <><br /><br /> </>}
            <button style={{border: "none" , background: "none"}} onClick={toggleIsFavorite}>{isFavorite ? <FavoriteOutlinedIcon style={{color:"#CC0000"}}/> : <FavoriteBorderOutlinedIcon style={{color:"#CC0000"}}/>}</button>
            <h3 style={{color: "#111111"}}>Total Price : <span style={{ fontSize: "1.45rem"}}>{item.quantity === 0 ? 0 : item.price_usd}</span> <span style={{ fontSize: "1rem", color: "#4B4B4B" }}>USD</span></h3>
            <h3 style={{color: "#111111"}}>Stock : <span style={{ fontSize: "1.35rem"}}>{item.max_stock} <span style={{ fontSize: "1.1rem", color:  "#C9A227", fontFamily: "serif"}}> U n i t s</span></span></h3>
          </div>
        </>
      )}

      {!user && (
        <>
          {/* תצוגה פשוטה לאורח שלא מחובר – רק מחיר וסטוק */}
          <h3 style={{color: "#111111"}}>Price : <span style={{ fontSize: "2rem" }}>{item.price_usd}</span> USD</h3>
          <h3 style={{ color: "#00010aff", textAlign: "center" }}>Stock: {item.stock}</h3>
        </>
      )}
    </Card>
  );
}
// "#4B4B4B"