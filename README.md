# Full Stack eCommerce System

A complete full-stack eCommerce application built with Java, Spring Boot, React, MySQL, REST APIs, and JWT authentication.

The project demonstrates end-to-end development of an eCommerce system, including backend business logic, database integration, secure authentication and authorization, and a React frontend.

## Key Features

- User registration and login
- Secure JWT-based authentication
- User authorization with Spring Security
- Product management
- Shopping cart functionality
- Favorites management
- Order management
- Complete purchase flow
- Admin functionality for managing products and application data
- RESTful API communication between frontend and backend
- MySQL database integration

## Technologies

### Backend

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- REST APIs
- JdbcTemplate
- MySQL
- Maven

### Frontend

- React
- React Router
- Axios
- JavaScript
- HTML
- CSS

### Development & Testing Tools

- Git & GitHub
- Postman
- Docker

## Project Structure

The project is organized into separate backend and frontend applications:

```text
fullStack-online-store/
│
├── backend-java+jwt/
│   └── web-store-backend/
│
└── frontend-react/
    └── web-store-frontend/
```

### Backend

The backend is built with Java and Spring Boot and is responsible for:

- REST API endpoints
- Business logic
- Authentication and authorization
- Product and order management
- Database operations using JdbcTemplate
- MySQL database integration

### Frontend

The frontend is built with React and communicates with the Spring Boot backend through REST APIs.

It provides the user interface for browsing products, managing favorites and the shopping cart, placing orders, and accessing application functionality according to user permissions.

## Security

Authentication and authorization are implemented using Spring Security and JWT.

The backend requires a `JWT_SECRET` environment variable.

Example:

```text
JWT_SECRET=your-secret-here
```

The actual secret should never be committed to the repository.

## Running the Project

### Backend

1. Configure the required MySQL database settings.
2. Set the `JWT_SECRET` environment variable.
3. Run the Spring Boot application.

### Frontend

Navigate to the frontend directory and install the dependencies:

```bash
npm install
```

Then start the React application:

```bash
npm start
```

## Project Purpose

This project was developed as a hands-on Full Stack development project, demonstrating the integration of a Java/Spring Boot backend with a React frontend and MySQL database.

It covers the complete development flow of an eCommerce application, from database and backend business logic to secure REST APIs and frontend integration.
