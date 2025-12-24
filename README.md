# Uber-backend

<img width="1536" height="1024" alt="ChatGPT Image Dec 24, 2025, 12_43_20 PM" src="https://github.com/user-attachments/assets/2132e9b3-62f2-4bf9-80dd-47077bfe4251" />

Navio is a full-stack, Uber-like ride booking platform built using Spring Boot, designed with real-time communication, scalable architecture, and modern backend practices.

It supports ride booking, driver matching, live chat, payments, wallets, and real-time notifications using WebSockets and Redis.

📌 Features

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

