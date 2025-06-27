# 🛍️ Final Capstone: Easy-App

A full-featured E-Commerce API built with Spring Boot. This backend powers a simplified online store application with secure JWT authentication, user profiles, shopping cart functionality, and admin management tools for products and categories.

---

## 🔧 Features

- 🔐 JWT Authentication & Role-Based Access
- 📦 Category & Product CRUD Operations
- 🛒 Persistent Shopping Cart (Per User)
- 👤 User Profile Management
- 📊 Admin-Only Actions for Secure Control
- 🌐 RESTful Endpoints (Testable in Postman)

---

## 🛠 Technologies Used

- **Java 17**
- **Spring Boot & Spring Security**
- **JWT (JSON Web Tokens)**
- **MySQL & JDBC**
- **BCrypt Password Hashing**

---

## 📋 API Routes

### 🔐 Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/register` | Register a new user |
| POST   | `/login`    | Authenticate and get a JWT token |

### 📦 Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/categories` | Get all categories |
| GET    | `/categories/{id}` | Get a single category by ID |
| GET    | `/categories/{id}/products` | Get all products in a category |
| POST   | `/categories` | Create a new category (admin only) |
| PUT    | `/categories/{id}` | Update a category (admin only) |
| DELETE | `/categories/{id}` | Delete a category (admin only) |

### 🛍️ Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/products` | Search products (by category, price, color) |
| GET    | `/products/{id}` | Get a product by ID |
| POST   | `/products` | Add a new product (admin only) |
| PUT    | `/products/{id}` | Update a product (admin only) |
| DELETE | `/products/{id}` | Delete a product (admin only) |

### 👤 Profiles
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/profile/{userId}` | Get a user’s profile |
| PUT    | `/profile/{userId}` | Update a user’s profile |

### 🛒 Shopping Cart
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/cart` | Get current user’s cart |
| POST   | `/cart/products/{productId}` | Add a product to cart |
| PUT    | `/cart/products/{productId}` | Update product quantity |
| DELETE | `/cart` | Clear the entire cart |

---

## 🧪 Postman Tests

### 🧾 Authentication
```json
POST /login
{
  "username": "admin",
  "password": "password"
}

Categories: 

POST /categories
Authorization: Bearer {admin_token}

Product:

{
  "name": "Kitchen",
  "description": "Kitchen appliances and accessories"
}

Profile:

PUT /profile/2
Authorization: Bearer {user_token}
{
  "firstName": "Alex",
  "lastName": "Smith",
  "email": "alex@example.com"
}

Cart:

POST /cart/products/4
Authorization: Bearer {user_token}

💡 Tip: Use Bearer tokens from the /login response when testing authorized endpoints.

