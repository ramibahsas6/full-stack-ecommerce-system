import '../styles/AdminDeleteUsers.css'
import React, { useContext, useEffect, useState } from 'react'
import UserContext from '../contexts/UserContext'
import { adminDeleteUser, adminFetchAllUsers } from '../services/ApiServices';
import { useNavigate } from 'react-router-dom';

const AdminDeleteUsers = ({ setPage }) => {
  const { user: currentUser } = useContext(UserContext);
  const [allUsers, setAllUsers] = useState([]);
  const [isRequestToGetAllUsersDone, setIsRequestToGetAllUsersDone] = useState(false);
  const navigate = useNavigate();

  // שולף את כל המשתמשים מהשרת ומסנן את המשתמש הנוכחי כדי שלא יוצג ברשימה
  const getAllUsers = async () => {
    try {
      const { data } = await adminFetchAllUsers();
      setAllUsers(data.filter(user => user.username !== currentUser.username));
    } catch (err) {
      console.log(err);
    }
    setIsRequestToGetAllUsersDone(true);
  }

  // useEffect מריץ את getAllUsers רק אם המשתמש מחובר והוא אדמין
  useEffect(() => {
    if (currentUser && currentUser.role === "ADMIN") {
      getAllUsers();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentUser]);

  // פונקציה שמוחקת משתמש אחרי אישור מנהל
  const handleDeleteUser = async (username) => {
    const isConfirmed = window.confirm(
      `Are you sure you want to delete ${username}'s account? This action cannot be undone.`
    );
    if (isConfirmed) {
      try {
        await adminDeleteUser(username);
        getAllUsers(); // מרענן את הרשימה אחרי מחיקה
        alert(`${username}'s account has been deleted successfully.`);
        navigate('/Admin'); // מחזיר לדף הראשי של האדמין
      } catch (err) {
        console.log(err);
      }
    }
  }

  return (
    <div className='admin-page'>
      {currentUser && currentUser.role === "ADMIN" &&
        <div>
          <ul className='user-list'>
            {allUsers.map(({ username, first_name, last_name, email, phone, country, city, role }) =>
              <li className='user-item' key={username}>
                <div className='user-info'>
                  <span>Username: <b>{username}</b></span>
                  <span>Name: <b>{first_name} {last_name}</b></span>
                  <span>Email: <b>{email}</b></span>
                  <span>Phone: <b>{phone}</b></span>
                  <span>Address: <b>{country}, {city}</b></span>
                  <span>Role: <b>{role}</b></span>
                </div>
                <div className='actions'>
                  {/* כפתור שמוחק את המשתמש הזה */}
                  <button
                    className='delete-btn'
                    onClick={() => handleDeleteUser(username)}
                  >
                    Delete User
                  </button>
                </div>
              </li>
            )}
          </ul>
          {!allUsers.length && isRequestToGetAllUsersDone && <h3>No users found</h3>}
        </div>
      }

      {/* מציג הודעת גישה לא מורשית אם המשתמש לא אדמין */}
      {(!currentUser || currentUser.role !== "ADMIN") &&
        <div>
          <h2 style={{ textAlign: "center", color: "red" }}>Unauthorized Access</h2>
          <h3>You are not authorized to access this page</h3>
        </div>
      }
    </div>
  )
}

export default AdminDeleteUsers;
