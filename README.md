# Chauffeur Backend

This repository combines the backend services and shared framework for the Chauffeur platform.

## Repository Structure

```
chauffeur-backend/
├── java-framework/          # Core shared framework and utilities
│   ├── pom.xml
│   └── src/
└── nucleus/                 # Microservices suite
    ├── booking-service/     # Ride booking and scheduling
    ├── chat-service/        # Chat functionality
    ├── fleet-service/       # Fleet and driver management
    ├── nucleus-app-boot/    # Application bootstrap module
    ├── user-service/        # User and authentication service
    ├── util-service/        # Shared utility service
    ├── whatsapp-service/    # WhatsApp messaging integration
    └── pom.xml
```

## Modules

### 1. `java-framework`
Shared Java framework providing cross-cutting concerns:
- Base entity mappings
- Security and authentication utilities
- Database / Hibernate configuration
- Common data transfer objects (DTOs) and helpers

### 2. `nucleus`
Microservices suite containing the core business logic for:
- **Booking Service**: Booking lifecycle, journey states, and pricing calculation
- **Fleet Service**: Driver, vehicle, and chauffeur tracking
- **User Service**: User profiles, preferences, and authentication
- **Communication Services**: Chat and WhatsApp integrations
- **Nucleus App Boot**: Spring Boot runner module

## Building and Running

Prerequisites:
- Java JDK 17+ (or JDK 21)
- Apache Maven 3.8+
- PostgreSQL

Build framework first, followed by nucleus:
```bash
# Build framework
cd java-framework
mvn clean install

# Build nucleus services
cd ../nucleus
mvn clean install
```
