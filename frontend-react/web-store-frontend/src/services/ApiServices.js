import axios from "axios";

// כתובת ה־API הראשית של השרת
const API_URL = "http://localhost:9000";

// יצירת משתמש חדש
export const createUser = (user) => {
    return axios.post(API_URL + "/users/register", user);
};

// התחברות משתמש והחזרת JWT
export const loginUser = (AuthenticationRequest) => {
    return axios.post(API_URL + "/authenticate", AuthenticationRequest);
};

// מחזיר headers עם ה־JWT לשליחה לשרת
export const getCurrentUserHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        headers: {
            Authorization: `Bearer ${token}`
        }
    }
};

// headers מותאמים במיוחד לבקשות search
export const getCurrentUserHeadersForSearch = () => {
  const token = localStorage.getItem("token");
  return {
    Authorization: `Bearer ${token}`
  };
};

// headers מחוקים לפעולות DELETE עם axios
export const getCurrentUserHeadersForDelete = () => {
    const token = localStorage.getItem("token");
    return { Authorization: `Bearer ${token}` };
};

// מחזיר את פרטי המשתמש הנוכחי לפי הטוקן
export const getUser = () => {
    return axios.get(API_URL + "/users", getCurrentUserHeaders());
};

// מביא את כל הפריטים (לא דורש משתמש)
export const getAllItems = () => {
    return axios.get(API_URL + "/items/all");
};

// מביא פריטים מותאמים למשתמש (עם כמות במלאי)
export const getAllItemsResponse = () => {
    return axios.get(API_URL + "/items/user-items", getCurrentUserHeaders());
};

// הסרת פריט מההזמנה או הפחתת כמות
export const removeItem = (itemResponse, flag) => {
    return axios.post(`${API_URL}/order-items/${flag}`, itemResponse, getCurrentUserHeaders());
};

// הוספת פריט להזמנה או הגדלת כמות
export const addItem = (itemResponse, flag) => {
    return axios.post(`${API_URL}/order-items/${flag}`, itemResponse, getCurrentUserHeaders());
};

// הוספת פריט למועדפים
export const addToFavorites = (itemResponse) => {
    return axios.post(`${API_URL}/favorites`, itemResponse, getCurrentUserHeaders());
};

// הסרת פריט מהמועדפים (DELETE עם גוף)
export const removeFromFavorites = (itemResponse) => {
    return axios.delete(`${API_URL}/favorites`, {
        headers: getCurrentUserHeadersForDelete(),
        data: itemResponse
    });
};

// מביא את כל המועדפים
export const getFavorites = () => {
    return axios.get(`${API_URL}/favorites`, getCurrentUserHeaders());
};

// ביצוע חיפוש פריטים לפי עמוד (home / favorites וכו')
export const searchItems = (page, searchItemTitle) => {
  return axios.get(`${API_URL}/items/search/${page}`, {
    params: { search: searchItemTitle },
    headers: getCurrentUserHeadersForSearch()
  });
};

// מביא מחיר של פריט בודד לפי מזהה
export const getPriceByItemId = (itemId) => {
  return axios.get(`${API_URL}/items/get_price`, {
    params: { itemId }
  });
};

// מביא את כל ההזמנות של המשתמש
export const getAllOrders = () => {
  return axios.get(`${API_URL}/orders/all-orders`, getCurrentUserHeaders());
};

// קניית הזמנה – סוגר TEMP order
export const buyOrder = () => {
    return axios.put(`${API_URL}/orders`, null, getCurrentUserHeaders());
};

// מביא הזמנה ספציפית לפי מזהה
export const getUserOrder = (orderId) => {
  return axios.get(`${API_URL}/orders`, {
    params: { id: orderId },
    headers: getCurrentUserHeadersForSearch()
  });
};

// מחיקת חשבון משתמש
export const deleteUserRequest = () => {
  return axios.delete(API_URL + "/users", getCurrentUserHeaders());
};

// הוספת פריט לטבלת לאתר ע"י ה ADMIN
export const addNewItem = (item) => {
  return axios.post(`${API_URL}/admin`, item, getCurrentUserHeaders());
};

// מביא את כל המשתמשים
export const adminFetchAllUsers = () => {
    return axios.get(`${API_URL}/admin/all-users`, getCurrentUserHeaders());
}

// מחיקת משתמש
export const adminDeleteUser = (username) => {
    return axios.delete(`${API_URL}/admin/delete-user/${username}`, getCurrentUserHeaders());
}