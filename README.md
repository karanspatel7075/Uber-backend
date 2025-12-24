# 🚕 Navio – Uber-Like Ride Booking Backend

<img width="1536" height="1024" alt="Navio Architecture" src="https://github.com/user-attachments/assets/2132e9b3-62f2-4bf9-80dd-47077bfe4251" />

Navio is a **scalable, Uber-like ride booking backend system** built using **Spring Boot**.  
It is designed with **real-time communication**, **secure authentication**, and **modern backend architecture** to handle ride booking, driver matching, live chat, payments, wallets, and notifications.

---

## 📌 Key Highlights

- Real-time ride booking & tracking
- Driver matching using geo-location
- Secure JWT-based authentication
- WebSocket-powered live updates & chat
- Redis-based scalable messaging
- Stripe payment integration
- Clean layered architecture (Controller → Service → Repository)

---

## 👤 User & Driver Features

- Secure authentication & authorization (Spring Security + JWT)
- Role-based access control: **USER, DRIVER, ADMIN**
- Wallet system for riders
- Driver availability & live location tracking
- Admin onboarding & system monitoring

---

## 🚗 Ride Management

- Ride request & driver selection
- Pickup & drop locations with latitude & longitude
- Complete ride lifecycle management:
  - Requested
  - Accepted
  - Ongoing
  - Completed / Cancelled
- Real-time ride status updates

---

## 💬 Real-Time Chat System

- WebSocket-based private chat (Driver ↔ Rider)
- Redis Pub/Sub for horizontal scalability
- MongoDB for chat message persistence
- Supports multi-tab & multi-instance deployment

---

## 💳 Payments & Wallet

- Stripe Checkout integration
- Secure ride-based payment sessions
- Payment status tracking:
  - Pending
  - Success
  - Failed
- Wallet balance management & fare deduction

---

## 📡 Real-Time Infrastructure

- WebSockets (STOMP + SockJS)
- Redis used for:
  - Pub/Sub messaging
  - Live driver location tracking (Geo)
- MongoDB for chat data
- MySQL for transactional data

---

## 🔑 Backend Framework & Core Concepts

- **Spring Boot** – Rapid backend development & auto-configuration
- **Spring IOC Container** – Manages beans and lifecycle
- **Dependency Injection** – Loose coupling & scalability
- **Spring MVC Architecture** – Clean separation of concerns
- **Lombok** – Reduces boilerplate using annotations like  
  `@Getter`, `@Setter`, `@Builder`, etc.

---

## 🛡️ Security & Authentication

- Spring Security for API protection
- JWT-based stateless authentication
- Role-Based Access Control (RBAC):
  - Rider
  - Driver
  - Admin
- BCrypt password encryption
- Custom JWT filters for request validation

---

## 🌍 Ride Matching & Business Logic

- Rider can request a ride
- Nearest available drivers matched using location-based logic
- Driver availability tracking in real-time
- Efficient geo-distance calculations
- Modular strategy-based matching design

---

## ⚡ Real-Time Communication

- WebSocket (STOMP + SockJS) for:
  - Ride status updates
  - Notifications
  - Driver ↔ Rider messaging
- Redis Pub/Sub for:
  - Event broadcasting
  - Low-latency message delivery

---

## 🗄️ Database & Persistence

### Relational Database
- **MySQL**
  - Users, Drivers, Rides, Payments
- **Spring Data JPA**
  - Repository-based access
  - Custom JPQL & native queries
- **Hibernate ORM**
  - Optimized object-relational mapping

### NoSQL & In-Memory
- **MongoDB**
  - Chat message storage
- **Redis**
  - Location caching
  - Pub/Sub messaging
  - Fast in-memory access

---

## 🧪 Validation, Exception Handling & Logging

- Spring Bean Validation for request validation
- Global exception handling with meaningful API responses
- Logging & auditing using **Log4j**
- Tracks user actions, ride events, and system activity

---

## 📊 Monitoring & API Documentation

- **Spring Boot Actuator**
  - Health checks
  - Application metrics
- **Swagger (OpenAPI)**
  - Interactive REST API documentation
- **Postman Collections**
  - API testing & validation

---

## 📧 Email Notifications

- JavaMailSender integration
- OTP verification emails
- Ride acceptance notifications
- Important system alerts

---

## 🚢 Deployment (In Progress)

- Dockerized backend service
- Deployment planned on **Render**
- Cloud database support:
  - Neon PostgreSQL (planned for production)
- Designed for future **microservices scalability**

---

## 🧠 Design Patterns Used

- Strategy Pattern (Driver matching, fare calculation)
- Builder Pattern
- Factory Pattern
- Singleton Pattern

---

## 📄 License

This project is developed for **learning, demonstration, and portfolio purposes**.

---

### ⭐ If you like this project, don’t forget to star the repository!
