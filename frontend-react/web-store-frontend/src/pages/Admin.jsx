import React, { useState } from 'react'
import '../styles/Admin.css'
import AdminAddItem from '../components/AdminAddItem';
import AdminDeleteUsers from '../components/AdminDeleteUsers';

const Admin = () => {
  const [page, setPage] = useState("");

  const handleAddItem = () => {
    setPage("addItem");
  }

  const handleDeleteUsers = () => {
    setPage("deleteUsers");
  }

  return (
    <div style={{backgroundColor: "#878686ff" , minHeight: "100vh", fontSize: "1.2rem"}}>
      <h2 style={{color: "#000000ff", margin: "0", padding: "26px", letterSpacing: "0.04em"}}>
        Admin
      </h2>

      <button onClick={handleAddItem} style={{color: 'gold', background: "linear-gradient(90deg, #000000, #4c4b4bff)", height: "40px", width: "200px", borderRadius: '50%', fontSize: '1.3rem', marginRight: "20px", border: "2px solid #d4af37"}}>add Item</button>
      <button onClick={handleDeleteUsers} style={{color: 'red', background: "linear-gradient(90deg, #4c4b4bff, #000000)", height: "40px", width: "200px", borderRadius: '50%', fontSize: '1.3rem', marginLeft: "20px", border: "2px solid #d4af37"}}>Delete Users</button>

      {page === "addItem" && <AdminAddItem setPage={setPage}/>}
      {page === "deleteUsers" && <AdminDeleteUsers setPage={setPage} />}
      </div>
  )
}

export default Admin