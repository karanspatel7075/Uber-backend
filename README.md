# Uber-backend

<img width="1536" height="1024" alt="ChatGPT Image Dec 24, 2025, 12_43_20 PM" src="https://github.com/user-attachments/assets/2132e9b3-62f2-4bf9-80dd-47077bfe4251" />

Navio is a full-stack, Uber-like ride booking platform built using Spring Boot, designed with real-time communication, scalable architecture, and modern backend practices.

It supports ride booking, driver matching, live chat, payments, wallets, and real-time notifications using WebSockets and Redis.

# 📌 Features

# 👤 User & Driver

Secure authentication & authorization (Spring Security + JWT)

Role-based access (USER, DRIVER, ADMIN)

Wallet system for users

Driver availability & live location tracking



# 🚗 Ride Management

Ride request & driver selection

Pickup & drop location with latitude/longitude

Ride lifecycle:

Requested

Accepted

Ongoing

Completed


# 💬 Real-Time Chat System

WebSocket-based private chat

Redis Pub/Sub for horizontal scalability

Chat history stored in MongoDB

Multi-tab & multi-instance support



# 💳 Payments

Stripe Checkout integration

Ride-based payment sessions

Secure payment verification

Wallet balance management


# 📡 Real-Time Infrastructure

WebSockets (STOMP)

Redis for:

Pub/Sub messaging

Live driver tracking (Geo)

MongoDB for chat messages

MySQL for transactional data

# 🔑 Key Features

🚀 Backend Framework & Core Concepts

Spring Boot: Used for rapid backend development and auto-configuration.<br>
Spring IOC Container: Manages application components and lifecycle.<br>
Beans & Auto Configuration: Leveraged Spring Boot’s internal configurations.<br>
Dependency Injection: Extensively used for loose coupling and scalability.<br>
Spring MVC Architecture: Clear separation of Controller, Service, and Repository layers.<br>
Lombok: Reduced boilerplate code using annotations like @Getter, @Setter, @Builder, etc.<br>


🛡️ Security & Authentication

Spring Security: Implemented to secure REST APIs.<br>
JWT Authentication: Stateless authentication for users and drivers.<br>  
Role-Based Access Control (RBAC):<br>
Rider<br>
Driver<br>
Admin<br>
Password Encryption: Secure password hashing using BCrypt.<br>
Custom Security Filters: JWT validation and request authorization.<br>


🌍 Ride Management & Core Business Logic

Ride Request & Matching System:<br>
Riders can request rides.<br>
Nearest drivers matched using location-based logic.<br>
Ride Lifecycle Management:<br>
Ride Requested<br>
Ride Accepted<br>
Ride Started<br>
Ride Completed / Cancelled<br>
Driver Availability Tracking: Real-time driver status updates.<br>
Geo-location Handling: Efficient distance calculations and driver discovery.<br>


⚡ Real-Time Communication

WebSocket (STOMP + SockJS):<br>
Real-time ride updates<br>
Live ride status notifications<br>
Driver ↔ Rider communication<br>
Redis Pub/Sub:<br>
Message broadcasting<br>
Real-time event handling<br>
Low Latency Messaging: Optimized for instant updates.<br>


💬 Chat System

Real-Time Chat between Rider and Driver.<br>
WebSocket-Based Messaging.<br>
MongoDB used for storing chat messages.<br>
Scalable Message Storage independent of transactional data.<br>


💳 Payments & Wallet

Stripe Integration:<br>
Secure ride payment processing.<br>
Wallet Management System:<br>
Track user balances<br>
Ride fare deductions<br>
Payment Status Tracking:<br>
Pending<br>
Success<br>
Failed<br>


🗄️ Database & Persistence

MySQL:<br>
Core transactional data (Users, Drivers, Rides, Payments).<br>

MongoDB:<br>
Chat message storage.<br>

Redis (In-Memory):<br>
Location caching<br>
Pub/Sub messaging<br>
Session-like fast data access<br>

Spring Data JPA:<br>
Repository-based database access.<br>
Custom JPQL & native queries.<br>
Hibernate ORM:<br>
Object-Relational Mapping with optimized queries.<br>


🧪 Validation, Exception Handling & Logging

Spring Bean Validation:<br>
Request payload validation using annotations.<br>

Global Exception Handling:<br>
Centralized error handling with meaningful responses.<br>

Logging & Auditing:<br>
Implemented using Log4j.<br>
Tracks user actions, ride updates, and system events.<br>


📊 Monitoring & Documentation

Spring Boot Actuator:<br>
Health checks<br>
Metrics monitoring<br>
Swagger (OpenAPI):<br>
Interactive API documentation.<br>
Postman Collections:<br>
API testing and validation.<br>


📧 Email Notifications

JavaMailSender:<br>
OTP verification emails<br>
Ride acceptance notifications<br>
Important system alerts<br>


🚢 Deployment (In Progress)

Dockerized Application:<br>
Containerized backend service.<br>
Render Deployment:<br>
Hosting backend services.<br>
Cloud Database:<br>
Neon PostgreSQL (planned for production).<br>
Scalable Architecture:<br>
Designed to support microservices in future.<br>
