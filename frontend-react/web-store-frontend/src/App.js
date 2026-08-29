import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import NavBar from './components/NavBar';
import Home from './pages/Home';
import Login from './pages/Login';
import Registration from './pages/Registration';
import Admin from './pages/Admin';
import Favorites from './pages/Favorites';
import Orders from './pages/Orders';
import NotFound from './pages/NotFound';
import UserContext from './contexts/UserContext';
import { useEffect, useState } from 'react';
import { getUser } from './services/ApiServices';

// hello to rami's app
function App() {
  
  // state מרכזי של האפליקציה – המשתמש המחובר, סטטוס טעינה, וחיפוש
  const [user, setUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true); // נשתמש כדי להמתין לטעינת המשתמש מהשרת לפני הצגת האפליקציה
  const [onSearch, setOnSearch] = useState("");

  // useEffect שרץ פעם אחת בהעלאת האפליקציה – מנסה לטעון משתמש מתוך הטוקן בלוקאל סטורג'
  useEffect(() => {
    const loadUser = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        // אם אין טוקן בכלל – אין משתמש מחובר, מסיימים את מצב הטעינה
        setLoadingUser(false);
        return;
      }
      try {
        // מנסה להביא את פרטי המשתמש מהשרת לפי הטוקן
        const {data} = await getUser();
        setUser(data);
      } catch (err) {
        // אם הטוקן לא תקין / פג – מוחקים אותו מהלוקאל סטורג'
        console.log('Error loading user from token:', err);
        localStorage.removeItem('token');
      } finally {
        // בכל מקרה מסיימים את מצב הטעינה כדי להציג את האפליקציה
        setLoadingUser(false);
      }
    };

    loadUser();
  }, []);

  // בזמן שהמשתמש נטען – מציגים רק מסך "Loading..."
  if(loadingUser) 
    return <div>Loading...</div>;

  return (
    <div className="App">
      {/* UserContext.Provider עוטף את כל האפליקציה כדי שכל הקומפוננטות יוכלו לגשת ל-user ו-setUser */}
      <UserContext.Provider value={{ user, setUser}}>
      <Router>
        {/* NavBar מקבל את onSearch ו-setOnSearch כדי לשלוט בשדה החיפוש מהנב בר */}
        <NavBar onSearch={onSearch} setOnSearch={setOnSearch}/>
        <Routes>
          {/* מסלול הבית – מעביר את מצב החיפוש גם ל-Home */}
          <Route path="/" element={<Home onSearch={onSearch} setOnSearch={setOnSearch}/>} />
          {/* מסלול דינמי לפי פרמטר ב-URL, אפשר להשתמש בו בעתיד לחיפוש מקושר לראוטר */}
          <Route path="/:onSearch" element={<Home />} />
          <Route path="/login" element={<Login/>} />
          <Route path="/register" element={<Registration />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/favorites" element={<Favorites />} />
          <Route path="/orders" element={<Orders />} />
          {/* כל נתיב שלא קיים – עובר ל-NotFound */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
      </UserContext.Provider>
    </div>
  );
}

export default App;