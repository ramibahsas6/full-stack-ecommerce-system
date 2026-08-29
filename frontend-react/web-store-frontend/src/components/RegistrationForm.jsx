import React, { useState } from 'react'
import '../styles/RegistrationForm.css'
import { createUser} from '../services/ApiServices'
import { useNavigate } from 'react-router-dom'
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';

const RegistrationForm = () => {
    // סטייט של המשתמש החדש – כל השדות שנשלחים לשרת להרשמה
    const [currentUser, setCurrentUser] = useState({
        username: '',
        first_name: '',
        last_name: '',
        email: '',
        phone: '',
        country: '',
        city: '',
        password: '',
        role: 'USER'
    });

    // אובייקט של שגיאות ולידציה לכל שדה
    const [errors, setErrors] = useState({});
    // דגל שמסמן אם הטופס כולו תקין – משתמשים בו כדי לאפשר / לחסום כפתור Submit
    const [isFormVaild, setIsFormVaild] = useState(false);
    // שגיאה שמגיעה מהשרת (למשל: יוזר כבר קיים / מייל כפול)
    const [errorFromServer, setErrorFromServer] = useState('');
    // שליטה על הצגת / הסתרת הסיסמה
    const [showPassword, setShowPassword] = useState(false);
    
         const togglePasswordVisibility = () => {
            setShowPassword(!showPassword);
    }

    // onChange כללי – מעדכן את currentUser ומפעיל ולידציה לשדה שהשתנה
    const onChange = (e) => {
        const {name , value} = e.target;
        const updatedUser = { ...currentUser, [name]: value };
        setCurrentUser({...currentUser, [name]: value});
        validateField(name, value, updatedUser);
    }

    const navigate = useNavigate();

    // שליחת טופס – אם הטופס לא תקין (isFormValid=false) לא נשלח לשרת
    const handleSubmit = async (e) => {
        e.preventDefault();
        if(!isFormVaild) return;

        const payload = {
    ...currentUser,
    username: currentUser.username.trim().replace(/\s+/g, ' '),
    first_name: currentUser.first_name.trim().replace(/\s+/g, ' '),
    last_name: currentUser.last_name.trim().replace(/\s+/g, ' '),
    email: currentUser.email.trim().replace(/\s+/g, ' '),
    phone: currentUser.phone.trim().replace(/\s+/g, ' '),
    country: currentUser.country.trim().replace(/\s+/g, ' '),
    city: currentUser.city.trim().replace(/\s+/g, ' '),
    password: currentUser.password.trim().replace(/\s+/g, ' '),
    role: currentUser.role // לרוב לא צריך שינוי ברול
};
        
        try {
        // ניסיון יצירת משתמש חדש בשרת
        await createUser(payload);
        resetForm();
        // אחרי הרשמה מוצלחת – ניווט לעמוד ה-Login
        navigate('/login');
        } catch (err) {
                  // שגיאות לוגיות מהשרת (ולידציה בצד השרת)
                  if(err.response?.status === 400 || err.response?.status === 500){
                setErrorFromServer(err.response.data);
            }
            // שגיאת רשת – למשל אין חיבור
            if(err.code === 'ERR_NETWORK'){
                setErrorFromServer(err.message);
            }
            setTimeout(() => {
                setErrorFromServer('');
            }, 3500);
        }
    }

    // איפוס הטופס – מחזיר את currentUser לערכים ריקים
    const resetForm =() => setCurrentUser({
    username: '',
    first_name: '',
    last_name: '',
    email: '',
    phone: '',
    country: '',
    city: '',
    password: '',
    role: 'USER'
});

    // ביטויים רגולריים לולידציה של: username, password, email, phone
    const usernameRegex = /^[a-zA-Z][a-zA-Z0-9]{3,23}$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%])[A-Za-z\d!@#$%]{8,24}$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^[0-9]*$/;

       const validateField = (name, value, updatedUser) => {
        let error = "";
        // שדות חובה – אם ריק, מחזירים הודעת שגיאה
        if (!value.trim() && ["first_name", "last_name", "email", "username", "password"].includes(name)) {
            error = `${name.replace("_", " ")} is required.`;
        } else if (name === "username" && !usernameRegex.test(value)) {
            // ולידציה ל-username – חייב להתחיל באות ולהיות באורך 4–24
            error = "Username must be 4-24 characters long and start with a letter.";
        } else if (name === "password" && !passwordRegex.test(value)) {
            // ולידציה לסיסמה – אות גדולה, קטנה, מספר ותו מיוחד
            error = "Password must be 8-24 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
        } else if (name === "email" && !emailRegex.test(value)) {
            // ולידציה למבנה אימייל
            error = "Invalid email format.";
        } else if (name === "phone" && value.trim() && !phoneRegex.test(value)) {
            // אם הוזן טלפון – חייב להכיל רק ספרות
            error = "Phone number should only contain numbers.";
        }
        setErrors({ ...errors, [name]: error });
        
        // בדיקה אם כל שדות החובה מלאים ואם אין שגיאות בכלל → הטופס תקין
        const { first_name, last_name, email, username, password } = updatedUser;
        setIsFormVaild(
            Boolean(first_name && last_name && email && username && password) &&
            Object.values(errors).every(error => !error)
        );
    }

  return (
    <div>
        <form className='registration-form' onSubmit={handleSubmit}>
            <input
                type="text"
                name="first_name"
                placeholder='First Name'
                value={currentUser.first_name}
                onChange={onChange}
                className={errors.first_name ? "input-error" : ""}
            />
            {errors.first_name && <p className='error-text'>{errors.first_name}</p>}
            <input
                type="text"
                name="last_name"
                placeholder='Last Name'
                value={currentUser.last_name}
                onChange={onChange}
                className={errors.last_name ? "input-error" : ""}
            />
            {errors.last_name && <p className='error-text'>{errors.last_name}</p>}
            <input
                type="email"
                name="email"
                placeholder='Email'
                value={currentUser.email}
                onChange={onChange}
                className={errors.email ? "input-error" : ""}
            />
            {errors.email && <p className='error-text'>{errors.email}</p>}
            <input
                type="tel"
                name="phone"
                placeholder='Phone'
                value={currentUser.phone}
                onChange={onChange}
                className={errors.phone ? "input-error" : ""}
            />
            {errors.phone && <p className='error-text'>{errors.phone}</p>}
            <input
                type="text"
                name="country"
                placeholder='Country'
                value={currentUser.country}
                onChange={onChange}
            />
            <input
                type="text"
                name="city"
                placeholder='City'
                value={currentUser.city}
                onChange={onChange}
            />
            <input
                type="text"
                name="username"
                placeholder='User Name' 
                value={currentUser.username}
                onChange={onChange}
                className={errors.username ? "input-error" : ""}
            />
            {errors.username && <p className='error-text'>{errors.username}</p>}
    <div style={{ position: "relative", marginBottom: "1rem" }}>
                <input
                    type={showPassword ? "text" : "password"}
                    placeholder='Password'
                    name='password'
                    value={currentUser.password}
                    onChange={(e) => setCurrentUser({ ...currentUser, password: e.target.value })}
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
            {errors.password && <p className='error-text'>{errors.password}</p>}
            <select name="role" value={currentUser.role} placeholder='Role' onChange={onChange}>
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
            </select>
            {errorFromServer && <p className='error-text'>{errorFromServer}</p>}
            <button type="submit" disabled={!isFormVaild} >Submit</button>
            <button type="reset" onClick={() => resetForm()}>Reset</button>
            <button type="button" onClick={() => navigate('/login')}>Login</button>
        </form>
    </div>
  )
}

export default RegistrationForm
