import React, { useContext, useEffect } from 'react'
import '../styles/LoginForm.css'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { loginUser, getUser } from '../services/ApiServices'
import UserContext from '../contexts/UserContext'
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';

const LoginForm = () => {
    const navigate = useNavigate();
    // סטייט לשמירת שם משתמש וסיסמה מהטופס
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    // הקשר גלובלי של המשתמש – כאן נשמור את המשתמש המחובר אחרי login
    const { user,setUser} = useContext(UserContext);
    // הודעת שגיאה למקרה של פרטים שגויים או בעיית רשת
    const [error, setError] = useState("");
    // שליטה על הצגת / הסתרת הסיסמה (עין פתוחה / סגורה)
    const [showPassword, setShowPassword] = useState(false);

    // החלפת מצב תצוגת הסיסמה בין "text" ל-"password"
    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    }

    // טיפול בשליחת הטופס – ניסיון להתחבר, שמירת הטוקן, משיכת פרטי המשתמש והפניה ל-Home
    const handleSubmit = async (e) => {
        try{
        e.preventDefault();
         if (!username.trim() || !password.trim()) {
            // ולידציה בסיסית – לא מאפשרים שליחה בלי שם משתמש או סיסמה
            setError("Username and password are required.");
            setTimeout(() => {
                setError('');
            }, 3500);
            return;
        }
        // שליחת בקשת התחברות לשרת
        const {data} = await loginUser({username, password});
        console.log(data.jwt);
        // שמירת ה-JWT בלוקאל סטורג' כדי להשתמש בו בבקשות הבאות
        localStorage.setItem('token', data.jwt);
        }catch(err){
               // טיפול בשגיאות מהשרת – הרשאה / שגיאה לוגית
               if (err.response?.status === 403 || err.response?.status === 500) {
                setError(err.response.data);
            }
            // טיפול בשגיאת רשת (למשל אין חיבור אינטרנט)
            if (err.code === 'ERR_NETWORK') {
                setError(err.message);
            }
            setTimeout(() => {
                setError('');
            }, 3500);
            return;
        }
        try{
            // אחרי login מוצלח – מושכים את פרטי המשתמש ומעדכנים את ה-Context
            const {data} = await getUser();
            setUser(data);
            // ניווט לעמוד הבית אחרי התחברות מוצלחת
            navigate('/');
        }catch(err){
            console.log(err);
        } 
    }

    // לוג קטן לדיבוג – לראות מתי ה-UserContext מתעדכן
    useEffect(() => {
  console.log('UserContext updated:', user);
}, [user]);


    // איפוס הטופס – מחיקת שם משתמש וסיסמה מהסטייט
    const ResetForm = () => {
        setUsername('');
        setPassword('');
    }

  return (
    <form className="login-form" onSubmit={handleSubmit}>
      <input 
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        />
        <div style={{ position: "relative", marginBottom: "1rem" }}>
                <input
                    type={showPassword ? "text" : "password"}
                    placeholder='Password'
                    name='password'
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <span
                    onClick={togglePasswordVisibility}
                    style={{
                        position: "absolute",
                        right: "20px",
                        top: "40%",
                        transform: "translateY(-50%)",
                        cursor: "pointer"
                    }}
                >
                    {showPassword ? <VisibilityIcon style={{ fontSize: "20px", color: "gold" }} /> : <VisibilityOffIcon style={{ fontSize: "20px", color: "gold" }} />}
                </span>
            </div>
       {error && <p className='error-text'>{error}</p>}
      <button type="submit">Login</button>
      <button type="reset" onClick={ResetForm}>Reset</button>
      <button type="button" onClick={() => navigate('/register')}>Register</button>
    </form>
  )
}

export default LoginForm