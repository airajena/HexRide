# HexRide - Distributed Ride-Sharing Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-7.5-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**A production-grade microservices architecture for real-time ride-sharing**

[Architecture](#architecture) • [Features](#features) • [Quick Start](#quick-start) • [API Documentation](#api-documentation) • [Tech Stack](#tech-stack)

</div>

---

## 🎯 Overview

HexRide is a **distributed ride-sharing platform** built with microservices architecture, demonstrating enterprise-grade patterns for handling real-time geospatial matching, event-driven communication, and high-availability systems.

This project showcases:
- **8 independently deployable microservices**
- **Real-time driver matching** using Uber's H3 spatial indexing
- **Event-driven architecture** with Apache Kafka
- **Distributed locking** for concurrent ride assignments
- **Circuit breaker patterns** for fault tolerance
- **Full observability stack** (Prometheus, Grafana, Zipkin, ELK)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                 CLIENTS                                          │
│                    (Mobile Apps, Web, Third-Party Integrations)                  │
└─────────────────────────────────────┬───────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            API GATEWAY (8080)                                    │
│              Rate Limiting │ Circuit Breaker │ Load Balancing                   │
└─────────────────────────────────────┬───────────────────────────────────────────┘
                                      │
                          ┌───────────┼───────────┐
                          ▼           ▼           ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          EUREKA SERVER (8761)                                    │
│                         Service Discovery & Registry                            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
        ┌──────────────┬──────────────┼──────────────┬──────────────┐
        ▼              ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│    User      │ │   Driver     │ │    Ride      │ │  Location    │ │  Matching    │
│   Service    │ │   Service    │ │   Service    │ │   Service    │ │   Service    │
│   (8081)     │ │   (8083)     │ │   (8082)     │ │   (8084)     │ │   (8085)     │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
        │              │              │              │              │
        └──────────────┴──────────────┼──────────────┴──────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            APACHE KAFKA                                          │
│     ride.requested │ ride.status.changed │ driver.assigned │ location.updated   │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
                            ┌──────────────────┐
                            │    WebSocket     │
                            │     Service      │
                            │     (8086)       │
                            └──────────────────┘
```

---

## ✨ Features

### Core Business Features
| Feature | Description |
|---------|-------------|
| 🔐 **JWT Authentication** | Secure stateless authentication with access/refresh tokens |
| 🚗 **Real-time Matching** | H3 hexagonal grid-based driver matching within seconds |
| 📍 **Live Tracking** | WebSocket-based real-time driver location updates |
| 💰 **Dynamic Pricing** | Surge pricing based on demand per H3 cell |
| 🔄 **Ride Lifecycle** | Complete state machine from request to completion |
| 🔒 **OTP Verification** | 4-digit PIN for secure ride start |

### Technical Features
| Feature | Description |
|---------|-------------|
| ⚡ **Rate Limiting** | Redis-backed token bucket (10 req/s per IP) |
| 🔌 **Circuit Breaker** | Resilience4j for fault tolerance |
| 📊 **Observability** | Prometheus metrics, Grafana dashboards, Zipkin tracing |
| 📝 **Centralized Logging** | ELK Stack (Elasticsearch, Logstash, Kibana) |
| 🔄 **Event Sourcing** | Kafka for reliable event streaming |
| 🔐 **Distributed Locks** | Redis SETNX for concurrent ride assignment |

---

## 🚀 Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.9+

### Option 1: Full Docker Deployment
```bash
# Clone the repository
git clone https://github.com/yourusername/hexride.git
cd hexride

# Build and start all services
docker-compose up --build
```

### Option 2: Local Development (Hybrid)
```bash
# Start infrastructure only (PostgreSQL, Redis, Kafka, etc.)
.\dev\start-infra.bat

# Build all modules
.\dev\build-all.bat

# Start all services locally
.\dev\start-services.bat
```

### Access Points
| Service | URL |
|---------|-----|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Grafana | http://localhost:3000 (admin/admin) |
| Kibana | http://localhost:5601 |
| Zipkin | http://localhost:9411 |

---

## 📡 API Documentation

### Authentication
```bash
# Register a new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "rider@hexride.com",
    "password": "Password123!",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+15551234567",
    "userType": "RIDER"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+15551234567",
    "password": "Password123!"
  }'
```

### Ride Flow
```bash
# 1. Get fare estimate
curl -X POST http://localhost:8080/api/v1/fares/estimate \
  -H "Authorization: Bearer <token>" \
  -d '{
    "pickupLocation": {"latitude": 28.6139, "longitude": 77.2090},
    "dropoffLocation": {"latitude": 28.5355, "longitude": 77.2510},
    "rideType": "STANDARD"
  }'

# 2. Create ride with fare ID
curl -X POST http://localhost:8080/api/v1/rides \
  -H "Authorization: Bearer <token>" \
  -d '{
    "riderId": "user-uuid",
    "fareId": "fare_abc123",
    "pickupLocation": {...},
    "dropoffLocation": {...},
    "rideType": "STANDARD"
  }'
```

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 21** | Core language with virtual threads support |
| **Spring Boot 3.2** | Microservices framework |
| **Spring Cloud Gateway** | API Gateway with reactive support |
| **Spring Security + JWT** | Authentication & authorization |
| **Spring Data JPA** | Database abstraction |
| **Spring WebSocket** | Real-time communication |

### Data Layer
| Technology | Purpose |
|------------|---------|
| **PostgreSQL 15** | Primary database for transactional data |
| **Redis 7** | Caching, rate limiting, geospatial data |
| **Apache Kafka** | Event streaming & async messaging |
| **H3 (Uber)** | Hexagonal geospatial indexing |

### Infrastructure
| Technology | Purpose |
|------------|---------|
| **Netflix Eureka** | Service discovery |
| **Resilience4j** | Circuit breaker, retry, rate limiter |
| **Docker Compose** | Container orchestration |
| **Prometheus** | Metrics collection |
| **Grafana** | Metrics visualization |
| **Zipkin** | Distributed tracing |
| **ELK Stack** | Centralized logging |

---

## 📁 Project Structure

```
hexride/
├── api-gateway/          # API Gateway with rate limiting
├── eureka-server/        # Service discovery
├── user-service/         # Authentication & user management
├── driver-service/       # Driver profiles & availability
├── ride-service/         # Ride booking & lifecycle
├── location-service/     # Real-time location tracking
├── matching-service/     # Driver matching algorithm
├── websocket-service/    # Real-time push notifications
├── common/               # Shared DTOs, events, utilities
├── infrastructure/       # Config files for Prometheus, Grafana, etc.
├── dev/                  # Local development scripts
├── docs/                 # Service documentation
└── docker-compose.yml    # Production deployment
```

---

## 🔄 User Flow

### Complete Ride Journey

```
1. RIDER REGISTRATION
   └── POST /api/v1/auth/register → JWT tokens issued

2. FARE ESTIMATION
   └── POST /api/v1/fares/estimate → Fare cached in Redis (2 min TTL)

3. RIDE REQUEST
   └── POST /api/v1/rides → Kafka: ride.requested

4. DRIVER MATCHING (Async)
   └── matching-service consumes event
   └── H3 k-ring search for nearby drivers
   └── Score & rank by distance, rating, acceptance rate
   └── Acquire distributed lock → Assign driver
   └── Kafka: driver.assigned

5. REAL-TIME NOTIFICATIONS
   └── websocket-service pushes to rider/driver apps
   └── Status: REQUESTED → ACCEPTED

6. DRIVER EN ROUTE
   └── Location updates every 5 seconds
   └── Kafka: driver.location.updated → WebSocket broadcast

7. DRIVER ARRIVES
   └── PUT /api/v1/rides/{id}/driver-arrived
   └── Rider receives notification with OTP

8. RIDE STARTS
   └── PUT /api/v1/rides/{id}/start?otp=1234
   └── OTP verified → Status: IN_TRANSIT

9. RIDE COMPLETES
   └── PUT /api/v1/rides/{id}/complete
   └── Final fare calculated → Status: COMPLETED
```

---

## 📊 Observability

### Distributed Tracing (Zipkin)
Every request gets a trace ID that follows it across all services:
```
API Gateway → User Service → Database
     └── trace-id: abc123
```

### Metrics (Prometheus + Grafana)
- Request latency percentiles (p50, p95, p99)
- Error rates per service
- Circuit breaker states
- JVM metrics

### Centralized Logging (ELK)
All services log in JSON format with correlation IDs:
```json
{
  "timestamp": "2026-01-08T10:15:00Z",
  "service": "ride-service",
  "correlationId": "abc123",
  "message": "Ride created",
  "rideId": "ride-456"
}
```

---

## 🧩 Design Decisions

| Decision | Why |
|----------|-----|
| **H3 over Geohash** | Uniform hexagonal cells, better for proximity queries, Uber's proven production system |
| **Kafka over RabbitMQ** | Higher throughput, replay capability, better for event sourcing |
| **Redis for Locations** | Sub-millisecond latency, TTL for stale data, SET operations for H3 cells |
| **JWT over Sessions** | Stateless, scalable, mobile-friendly |
| **Eureka over Consul** | Better Spring Cloud integration, simpler setup |
| **Circuit Breaker** | Prevents cascade failures, graceful degradation |

---

## 🏆 Challenges Solved

1. **Race Conditions in Matching**
   - Problem: Multiple drivers accepting same ride
   - Solution: Redis distributed locks with SETNX

2. **Real-time Location at Scale**
   - Problem: High-frequency GPS updates
   - Solution: H3 indexing + Redis SET per cell

3. **Surge Pricing**
   - Problem: Dynamic pricing by area
   - Solution: Per-H3-cell surge multipliers in Redis

4. **Stale Driver Data**
   - Problem: Offline drivers appearing in search
   - Solution: TTL on Redis location keys (5 min)

---

## 📄 Documentation

Detailed documentation for each service:
- [Eureka Server](docs/eureka-server.md)
- [API Gateway](docs/api-gateway.md)
- [User Service](docs/user-service.md)
- [Driver Service](docs/driver-service.md)
- [Ride Service](docs/ride-service.md)
- [Location Service](docs/location-service.md)
- [Matching Service](docs/matching-service.md)
- [WebSocket Service](docs/websocket-service.md)
- [Common Module](docs/common-module.md)

---

## 👤 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Name](https://linkedin.com/in/yourname)
- Email: your.email@example.com

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**⭐ Star this repo if you find it helpful!**

</div>
