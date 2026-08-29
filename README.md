Full Stack Online Store

A full-stack online store application built with Java Spring Boot and React.

Configuration

✔ The backend requires a JWT_SECRET environment variable.

Example:

JWT_SECRET=your-secret-here

Technologies:

Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* MySQL
* JdbcTemplate
* REST API

Frontend

* React
* React Router
* Axios
* JavaScript
* HTML & CSS

Project Structure

fullStack-inline-store/
├── backend-java+jwt/
│   └── web-store-backend/
│
└── frontend-react/
    └── web-store-frontend/

Features

* User registration and login
* JWT-based authentication
* User authorization
* RESTful API
* Product management
* Order management
* React frontend connected to the Spring Boot backend
* MySQL database integration

Configuration

The backend requires a JWT_SECRET environment variable.

Example:

JWT_SECRET=your-secret-here

The secret should not be committed to the repository.

Running the Project

Backend

Configure the required database settings and JWT_SECRET, then run the Spring Boot application.

Frontend

Install the dependencies:

npm install

Then start the React application:

npm start
