import React, { useState } from 'react'
import '../styles/AdminAddItem.css'
import { addNewItem } from '../services/ApiServices';

const AdminAddItem = ({setPage}) => {

    // אובייקט של הטופס
    const [item, setItem] = useState({
    title: "",
    description: "",
    photo_url: "",
    price_usd: "",
    stock: ""
});

  // אובייקט של שגיאות ולידציה לכל שדה
    const [errors, setErrors] = useState({});

    // דגל שמסמן אם הטופס כולו תקין – משתמשים בו כדי לאפשר / לחסום כפתור Submit
    const [isFormVaild, setIsFormVaild] = useState(false);

// שגיאה שמגיעה מהשרת (למשל: מוצר כבר קיים / מייל כפול)
    const [errorFromServer, setErrorFromServer] = useState('');

    // onChange כללי – מעדכן את item ומפעיל ולידציה לשדה שהשתנה
const onChange = (e) => {
    const { name, value } = e.target;
    const updatedItem = { ...item, [name]: value };
    setItem(updatedItem);
    validateField(name, value, updatedItem);
};

// שליחת נתונים לשרת
const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(isFormVaild);
    
    if(!isFormVaild) return;

const payload = { // שליחת נתונים לשרת כמו שמצפה ולא כמו שהגיש הלקוח
    ...item,
    title: item.title.trim().replace(/\s+/g, ' '),
    description: item.description.trim().replace(/\s+/g, ' '),
    photo_url: item.photo_url.trim().replace(/\s+/g, ' '),
    price_usd: parseFloat(item.price_usd),
    stock: parseInt(item.stock)
};


    try{
        const { data } = await addNewItem(payload);
        if(data === 0)
        {
            alert("Item can't be added.");
            return;
        }

        handleReset();
        alert("Item added successfully.");
        setPage("");
    }catch(err){
                // שגיאות לוגיות מהשרת (ולידציה בצד השרת)
            if(err.response?.status === 400){
                setErrorFromServer("this item already exists.");
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

// איפוס הטופס
const handleReset = () => {
    setItem({
        title: "",
        description: "",
        photo_url: "",
        price_usd: "",
        stock: ""
    });
}

// ריגיולר איקספרישינז לשדות 
    const titleRegix = /^[A-Za-z0-9 ]{3,30}$/;
    const priceUsdRegex = /^(?:1500000(?:\.00?)?|(?:[0-9]|[1-9][0-9]{0,5})(?:\.[0-9]{1,2})?)$/
    const stockRegex = /^(?:[0-9]|[1-9][0-9]{1,3}|10000)$/;

// ולידציה לכל שדה
    const validateField = (name, value, updatedItem) => {
    let error = "";

    if(!value.trim() && ["title", "photo_url", "price_usd", "stock"].includes(name)){
        error = `${name.replace("_", " ")} is required.`;
    } else if(name === "title" && !titleRegix.test(value)){
        error = "Title must be 3-30 characters long.";
    } else if(name === "price_usd" && !priceUsdRegex.test(value)){
        error = "Price USD must be a number from 0 to 1500000.";
    } else if(name === "stock" && !stockRegex.test(value)){
        error = "Stock must be a number from 0 to 10000.";
    }

    // מעדכן את השגיאות
    const updatedErrors = { ...errors, [name]: error };
    setErrors(updatedErrors);

    const { title, description, photo_url, price_usd, stock } = updatedItem;

    // מעדכן את isFormVaild
    setIsFormVaild(
        Boolean(title && description && photo_url && price_usd && stock) &&
        Object.values(updatedErrors).every(err => !err)
    );
};


  return (
    <div>
        <form className="formAddItem" action="" onSubmit={handleSubmit}>
            <input 
                type="text"
                name="title"
                placeholder="Title"
                value={item.title}
                onChange={onChange}
                className={errors.title ? "input-error" : ""}
            />
            {errors.title && <p className='error-text'>{errors.title}</p>}
            <input 
                type="text"
                name="description"
                placeholder="Description"
                value={item.description}
                onChange={onChange}
                className={errors.description ? "input-error" : ""}
            />
            {errors.description && <p className='error-text'>{errors.description}</p>}
            <input 
                type="text"
                name="photo_url"
                placeholder="Photo URL"
                value={item.photo_url}
                onChange={onChange}
                className={errors.photo_url ? "input-error" : ""}
            />
            {errors.photo_url && <p className='error-text'>{errors.photo_url}</p>}
            <input 
                type="text"
                name="price_usd"
                placeholder="Price USD"
                onChange={onChange}
                className={errors.price_usd ? "input-error" : ""}
            />
            {errors.price_usd && <p className='error-text'>{errors.price_usd}</p>}
            <input 
                type="text"
                name="stock"
                placeholder="Stock"
                onChange={onChange}
                className={errors.stock ? "input-error" : ""}
            />
            {errors.stock && <p className='error-text'>{errors.stock}</p>}
            {errorFromServer && <p className='error-text'>{errorFromServer}</p>}
            <button type="submit">Add Item</button>
            <button type="button" onClick={handleReset}>Reset</button>
        </form>
    </div>
  )
}

export default AdminAddItem
