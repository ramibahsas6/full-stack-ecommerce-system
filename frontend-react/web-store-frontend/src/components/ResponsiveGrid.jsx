import React, { useContext, useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import ImgMediaCard from './ImgMediaCard';
import UserContext from '../contexts/UserContext';
import { getAllItems, getAllItemsResponse, getFavorites, searchItems } from '../services/ApiServices';

// קומפוננטה כללית שמציגה גריד של כרטיסים לפי מקור (home / favorites / search)
export default function ResponsiveGrid({ from , searchItemsResponses, onSearch}) {
  const { user } = useContext(UserContext);

  // כל הפריטים מהשרת כשאין משתמש מחובר (אורח)
  const [allItems, setAllItems] = useState([{ id: 0, title: '', photo_url: '', price_usd: 0.0, stock: 0 }]);

  // מיפוי של מחירים לפי itemId – משמש כדי להציג מחיר בעבודה עם ItemResponse
  const [priceUsdArray, setPriceUsdArray] = useState([]);

  // רשימת הפריטים בהתאמה למשתמש (כולל כמות בסל ומלאי זמין)
  const [allItemsResponse, setAllItemsResponse] = useState([{ item_id: 0, photo_url: '', title: '', max_stock: 0, quantity: 0, price_usd: 0.0 }]);

  // מערך של item_id שמסומן כמועדף – נוח לבדיקה מהירה ב־includes
  const [isFavoriteList, setIsFavoriteList] = useState([]);

  // רשימת המועדפים המלאה מהשרת (משמשת במסך favorites)
  const [favorites, setFavorites] = useState([]);

  // פריטים לתוצאות חיפוש (search)
  const [items, setItems] = useState([]);

  // פונקציה שמרעננת את הנתונים של הגריד בהתאם למצב (משתמש/אורח + מקור from)
  const refresh = async () => {
    try {
      // אם אין משתמש – נטען רק את כל הפריטים ללא מידע על סל
      if (!user) {
        const { data } = await getAllItems();
        setAllItems(data);
        return;
      }

      // אם לא מדובר במצב חיפוש – טוענים פריטים למשתמש + מחירים + מועדפים
      if(from !== "search"){ 
        try {
          const { data } = await getAllItemsResponse();
          if (data.length > 0) 
            setAllItemsResponse(data);
        
        } catch (err) { console.log(err); }

        // טעינת כל הפריטים הרגילים כדי לבנות מפת מחירים לפי id
        try {
          const { data } = await getAllItems();
          const priceMap = {};
          data.forEach(item => {
            priceMap[item.id] = item.price_usd;
          });
          setPriceUsdArray(priceMap);
        } catch (err) { console.log(err); }
      }
      else{
        // במצב search – מביאים פריטים מתאימים לפי החיפוש
        const { data } = await searchItems("home", onSearch.trim());
        setItems(data);
      }

      // תמיד נטען רשימת מועדפים כדי לדעת מה מסומן בכוכב
      try {
        const { data } = await getFavorites();
        setIsFavoriteList(data.map(item => item.item_id));
      } catch (err) { console.log(err); }
     
    } catch (err) { console.error(err); }
  };

  // ריענון נקודתי של רשימת המועדפים (למסך favorites)
  const refreshFavorites = async () => {
    if (!user) return;
    try {
      const { data } = await getFavorites();
      setFavorites(data);
    } catch (err) { console.log(err); }
  };

  // עדכון מקומי אחרי הסרה ממועדפים – בלי לחכות לריענון מלא מהשרת
  const removeFavoriteLocally = (itemId) => {
    setFavorites(prev => prev.filter(f => f.item_id !== itemId));
    setIsFavoriteList(prev => prev.filter(id => id !== itemId));
  };

  // אפקט שמריץ ריענון בכל פעם שמשתמש/מקור/תוצאות חיפוש משתנים
  useEffect(() => {
    if(user && from === "search"){
      setItems(searchItemsResponses);
    }

    refresh();
    refreshFavorites();
  }, [user, from, searchItemsResponses]);

  return (
    <Box sx={{ flexGrow: 2 }}>
      <Grid container spacing={{ xs: 1.5, md: 1.5 }} columns={{ xs: 4, sm: 8, md: 16 }}>
        {/* מצב אורח – מציגים רק את כל הפריטים בלי מידע על סל ומועדפים */}
        {!user && allItems.map(item => (
          <Grid key={item.id} size={{ xs: 2, sm: 4, md: 4 }}>
            <ImgMediaCard item={item} ifFavorite={false} priceUsd={0} refresh={refresh} isCurrentFavorite={false} />
          </Grid>
        ))}

        {/* משתמש מחובר – מצב רגיל (לא favorites ולא search) */}
        {user && from !== "favorites" && from !== "search" &&  allItemsResponse.map((item) => (
          <Grid key={item.item_id} size={{ xs: 2, sm: 4, md: 4 }}>
            <ImgMediaCard item={item} ifFavorite={false} priceUsd={priceUsdArray[item.item_id]} refresh={refresh} isCurrentFavorite={isFavoriteList.includes(item.item_id)} onFavoriteRemoved={removeFavoriteLocally} />
          </Grid>
        ))}

        {/* מסך מועדפים – מציג רק פריטים שאהבנו */}
        {user && from === "favorites" && favorites.map((item) => (
          <Grid key={item.item_id} size={{ xs: 2, sm: 4, md: 4 }}>
            <ImgMediaCard item={item} ifFavorite={true} priceUsd={[item.price_usd]} refresh={refreshFavorites} isCurrentFavorite={true} onFavoriteRemoved={removeFavoriteLocally} />
          </Grid>
        ))}

        {/* מסך חיפוש – תוצאות לפי onSearch */}
        {user && from === "search" && items.map((item) => (
          <Grid key={item.item_id} size={{ xs: 2, sm: 4, md: 4 }}>
            <ImgMediaCard item={item} ifFavorite={true} priceUsd={0} refresh={refresh} isCurrentFavorite={isFavoriteList.includes(item.item_id)} onFavoriteRemoved={removeFavoriteLocally} />
          </Grid>
        ))}

      </Grid>
    </Box>
  );
}