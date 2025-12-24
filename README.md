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

Spring Security: Implemented to secure REST APIs.

JWT Authentication: Stateless authentication for users and drivers.  

Role-Based Access Control (RBAC):

Rider

Driver

Admin

Password Encryption: Secure password hashing using BCrypt.

Custom Security Filters: JWT validation and request authorization.


🌍 Ride Management & Core Business Logic

Ride Request & Matching System:

Riders can request rides.

Nearest drivers matched using location-based logic.

Ride Lifecycle Management:

Ride Requested

Ride Accepted

Ride Started

Ride Completed / Cancelled

Driver Availability Tracking: Real-time driver status updates.

Geo-location Handling: Efficient distance calculations and driver discovery.


⚡ Real-Time Communication

WebSocket (STOMP + SockJS):

Real-time ride updates

Live ride status notifications

Driver ↔ Rider communication

Redis Pub/Sub:

Message broadcasting

Real-time event handling

Low Latency Messaging: Optimized for instant updates.


💬 Chat System

Real-Time Chat between Rider and Driver.

WebSocket-Based Messaging.

MongoDB used for storing chat messages.

Scalable Message Storage independent of transactional data.


💳 Payments & Wallet

Stripe Integration:

Secure ride payment processing.

Wallet Management System:

Track user balances

Ride fare deductions

Payment Status Tracking:

Pending

Success

Failed


🗄️ Database & Persistence

MySQL:

Core transactional data (Users, Drivers, Rides, Payments).

MongoDB:

Chat message storage.

Redis (In-Memory):

Location caching

Pub/Sub messaging

Session-like fast data access

Spring Data JPA:

Repository-based database access.

Custom JPQL & native queries.

Hibernate ORM:

Object-Relational Mapping with optimized queries.


🧪 Validation, Exception Handling & Logging

Spring Bean Validation:

Request payload validation using annotations.

Global Exception Handling:

Centralized error handling with meaningful responses.

Logging & Auditing:

Implemented using Log4j.

Tracks user actions, ride updates, and system events.


📊 Monitoring & Documentation

Spring Boot Actuator:

Health checks

Metrics monitoring

Swagger (OpenAPI):

Interactive API documentation.

Postman Collections:

API testing and validation.


📧 Email Notifications

JavaMailSender:

OTP verification emails

Ride acceptance notifications

Important system alerts


🚢 Deployment (In Progress)

Dockerized Application:

Containerized backend service.

Render Deployment:

Hosting backend services.

Cloud Database:

Neon PostgreSQL (planned for production).

Scalable Architecture:

Designed to support microservices in future.
