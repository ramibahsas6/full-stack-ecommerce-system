import React from 'react'
import '../styles/Favorites.css'
import ResponsiveGrid from '../components/ResponsiveGrid'

const Favorites = () => {
  return (
    <div className='favorites-container' style={{marginTop:"0.25px", textAlign:"center"}}>
      <p style={{fontSize: "0.55rem", marginTop: "0.2px", paddingTop: "0px"}}>❤️</p>
      <h2 style={{fontSize: "1.85rem", marginTop: "0.25px"}}> <span style={{color: "#939800ff"}}> F </span>a<span style={{color: "#939800ff"}}> v </span>o<span style={{color: "#939800ff"}}> r </span>i<span style={{color: "#939800ff"}}> t </span>s</h2>
        <ResponsiveGrid from="favorites" />
      </div>
  )
}

export default Favorites

// ❤️