// קומפוננטת דף הבית הראשי של MAVIX – אחראית על חיפוש, הודעות, ותצוגת הגריד
import React, { useContext , useEffect, useState} from 'react';
import '../styles/Home.css';
import UserContext from '../contexts/UserContext';
import ResponsiveGrid from '../components/ResponsiveGrid';
import { Link , useNavigate } from 'react-router-dom';
import { searchItems } from '../services/ApiServices';
import { useLocation } from 'react-router-dom';

const Home = ({onSearch, setOnSearch}) => {
  const { user } = useContext(UserContext);
  const [itemsResponses, setItemsResponses] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const [deleteMessage, setDeleteMessage] = useState("");

  // אפקט חיפוש – כל פעם שהטקסט ב־onSearch משתנה, אנחנו שולחים בקשה לשרת
  useEffect(() => {
    if(!onSearch || onSearch.trim() === "") {
      // אם אין טקסט חיפוש → מחזירים את הדף למצב "רגיל" (null = בלי תוצאות חיפוש)
      setItemsResponses(null);
      return;
    }
    
    const fetchSearch = async () => {
      try {
        // משתמש לא מחובר → אין חיפוש מותאם אישית
        if(!user) 
          return;

        // בקשה לשרת לחיפוש פריטים בעמוד הבית
        const { data } = await searchItems("home", onSearch.trim());
        setItemsResponses(data);
      } catch (err) {
        console.log(err);
        // במקרה של שגיאה – שומרים מערך ריק כדי לא להפיל את הדף
        setItemsResponses([]); // לא לשבור דף
      }
    };

    fetchSearch();
  }, [onSearch, navigate, user]);

  // אפקט שמאפס את שדה החיפוש כשנכנסים ל־"/" ויש משתמש מחובר

  useEffect(() => {
    if (user && location.pathname === "/")
      setOnSearch("");
  }, [location.pathname, user, setOnSearch]);

  // אפקט שמציג הודעה זמנית אם המשתמש מחק את החשבון (userDeleted מגיע מהניווט)

  useEffect(() => {
    if (location.state?.userDeleted) {
      setDeleteMessage("Your account was deleted successfully.");

      const timer = setTimeout(() => {
        setDeleteMessage("");
        // מחיקת ה־state מה־URL כדי שההודעה לא תחזור אחרי רענון
        navigate(location.pathname, { replace: true, state: {} });
      }, 3500);

      return () => clearTimeout(timer);
    }
  }, [location.state, navigate, location.pathname]);

  return (
    <div className="home" style={{ position: "relative" }}>
  {deleteMessage && <div className='delete-message'>{deleteMessage}</div>}

  {/* התמונה כרקע */}
  <img 
    src={`${process.env.PUBLIC_URL}/images/MAVIX.png`} 
    alt="MAVIX Logo" 
    style={{ width: "7%", height: "88px", display: "block", margin: "0 auto" }} 
  />

  {/* כותרות תמיד מעל התמונה */}
  <div style={{ position: "absolute", top: 0, left: 0, right: 0, textAlign: "center" }}>
    {user && (itemsResponses === null || onSearch === "") && (
      <h1 className='user-welcome'>
        Welcome <span style={{ color: "#aebb27ff" }}>{user.first_name} {user.last_name}</span>
      </h1>
    )}

    {user && itemsResponses !== null && (
      <h2>
        {itemsResponses.length === 0 
          ? <span style={{ color: "#b92828ff" }}>Results not found</span>
          : (
             <>
                <span style={{ color: "#111111" }}>Results for: </span>
                <span style={{ color: "#aebb27ff" }}>{onSearch}</span>
             </>
          )
        }
      </h2>
    )}

    {!user && (
      <>
        <h2>Welcome to <span style={{ color: "#aebb27ff" , fontSize:"1.5rem"}}>MAVIX</span> Store</h2>
        <h3 style={{fontSize:"1.4rem"}}>
        <Link to="/login"><span style={{ color: "#2f8500ff", marginRight: "6px" }}> Login</span></Link>{`to`} <Link to="/register"><span style={{ color: "#2f8500ff", marginLeft: "106px"}}>Register </span></Link>
        </h3>
      </>
    )}
  </div>

  {user && (itemsResponses === null || onSearch === "") && <ResponsiveGrid from="home" />}
  {user && itemsResponses !== null && <ResponsiveGrid from="search" searchItemsResponses={itemsResponses} onSearch={onSearch} />}
  {!user && <ResponsiveGrid />}
</div>


  );
}

export default Home;