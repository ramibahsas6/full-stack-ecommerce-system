import React from 'react'
import '../styles/NotFound.css'

const NotFound = () => {
  return (
    <div className='page-not-found'>
      <h1 className='notFound'>Page Not Found, please inter a valid</h1>
      <h1>URL or press the button to enter our Home page:</h1>
      <button><a href="/">MAVIX Home page</a></button>
    </div>
  )
}

export default NotFound