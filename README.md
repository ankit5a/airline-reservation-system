# Airline Reservation System

A multi-module Java backend for flight search, booking, and reservation management built with Ninja Framework, Guice, JPA/Hibernate, and PostgreSQL.

## Architecture

```text
airline-reservation/
├── airline-entities/       # JPA entity classes
├── airline-web-dtos/       # API request/response DTOs
├── airline-web/            # Controllers, facades, filters, routes, config
└── pom.xml                 # Parent Maven project
```

| Module | Responsibility |
|---|---|
| `airline-entities` | Domain model: `User`, `Flight`, `Booking`, `Passenger` |
| `airline-web-dtos` | API contracts: request/response DTO classes |
| `airline-web` | REST controllers, business logic, ACL/CORS filters, routing, config |

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Ninja 6.8.0 |
| DI | Google Guice + Guice Persist |
| ORM/JPA | Hibernate 4.x + `javax.persistence` |
| Database | PostgreSQL |
| JSON | Jackson |
| Password Hashing | BCrypt (`jbcrypt`) |
| Logging | Logback |
| Build | Maven |

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL running locally (or reachable)

## Configuration

Main runtime file:

- `/Users/ankitanand/airline-reservation/airline-web/src/main/resources/conf/application.conf`

Key properties:

```properties
application.modules.package=com.airline
ninja.port=8081

db.connection.url=jdbc:postgresql://localhost:5432/airline_db
db.connection.username=airline_user
db.connection.password=airline123
```

Example template:

- `/Users/ankitanand/airline-reservation/airline-web/src/main/resources/conf/example.application.conf`

## Database Setup

### 1) Create role/database

```sql
CREATE USER airline_user WITH PASSWORD 'airline123';
CREATE DATABASE airline_db OWNER airline_user;
```

### 2) Ensure schema permissions (if needed)

If you see permission errors on schema `public`, run:

```sql
GRANT USAGE, CREATE ON SCHEMA public TO airline_user;
ALTER SCHEMA public OWNER TO airline_user;
```

> The app uses `hibernate.hbm2ddl.auto=update`, so tables are auto-created/updated at startup when permissions are correct.

## Build and Run

From repo root:

```bash
mvn clean install
mvn -pl airline-web -DskipTests ninja:run
```

Server URL:

- `http://localhost:8081`

## API Response Format

Most endpoints return:

```json
{
  "success": true,
  "message": "...",
  "data": {}
}
```

Common status behavior:

- `200` success
- `400` invalid input
- `401` authentication required/invalid login
- `403` insufficient permissions (ACL)
- `404` entity not found
- `500` server error

## Authentication Model (Important for Testing)

Login writes values into server session (`userId`, `userRole`, `authToken`).
Protected endpoints use ACL filter checks.

For Postman/curl testing of protected routes:

1. Login first
2. Preserve session cookie
3. Optionally include `X-Auth-Token` from login response

## Endpoints and How to Test

Base URL used below:

```text
http://localhost:8081
```

## Auth Endpoints

### 1) Register

- Method/URL: `POST /api/auth/register`
- Body:

```json
{
  "username": "ankit_test_4",
  "password": "Password@123",
  "email": "ankit4@example.com",
  "fullName": "Ankit Anand",
  "role": "CUSTOMER"
}
```

Expected:
- `200 OK`
- `success=true`
- `data.id` populated

### 2) Login

- Method/URL: `POST /api/auth/login`
- Body:

```json
{
  "username": "ankit_test_4",
  "password": "Password@123"
}
```

Expected:
- `200 OK`
- `data.token` present
- session cookie set

Invalid credentials expected:
- `401 Unauthorized`

### 3) Logout

- Method/URL: `POST /api/auth/logout`
- Body: none

Expected:
- `200 OK`
- session cleared

## Flight Endpoints

### Public

### 4) Get all flights

- `GET /api/flights`

Expected:
- `200 OK`
- `data` is array

### 5) Get flight by id

- `GET /api/flights/{id}`

Expected:
- `200 OK` if exists
- `404` if not found

### 6) Search flights

- `GET /api/flights/search?origin=DELHI&destination=MUMBAI`

Expected:
- `200 OK`
- `data` filtered by origin/destination

### Admin only (ACL: `MANAGE_FLIGHTS`)

Requires logged-in admin session.

### 7) Create flight

- `POST /api/flights`
- Body:

```json
{
  "flightNumber": "AI-2026-001",
  "origin": "DELHI",
  "destination": "MUMBAI",
  "departureTime": "2026-03-05T10:00:00",
  "arrivalTime": "2026-03-05T12:15:00",
  "totalSeats": 180,
  "price": 5499.0
}
```

Expected:
- `200 OK` for admin
- `401/403` if not authorized

### 8) Update flight

- `PUT /api/flights/{id}`
- Body example:

```json
{
  "price": 5999.0,
  "status": "SCHEDULED",
  "availableSeats": 175
}
```

Expected:
- `200 OK` if admin + flight exists
- `404` if flight not found

### 9) Delete flight

- `DELETE /api/flights/{id}`

Expected:
- `200 OK` if admin + flight exists
- `404` if not found

## Booking Endpoints (Protected)

Requires logged-in session with required ACL actions.

### 10) Create booking (new passenger)

- `POST /api/bookings`
- Body:

```json
{
  "flightId": 1,
  "newPassenger": {
    "firstName": "Ankit",
    "lastName": "Anand",
    "passportNumber": "P1234567",
    "email": "ankit.passenger@example.com",
    "phone": "+919999999999"
  }
}
```

Expected:
- `200 OK` booking created
- `404` if flight missing
- `400` for invalid passenger payload

### 11) Create booking (existing passenger)

- `POST /api/bookings`
- Body:

```json
{
  "flightId": 1,
  "passengerId": 1
}
```

Expected:
- `200 OK`

### 12) Get booking by id

- `GET /api/bookings/{id}`

Expected:
- `200 OK` if found and permitted
- `404` if not found

### 13) Get all bookings

- `GET /api/bookings`

Expected:
- `200 OK` for roles with `VIEW_ALL_BOOKINGS` (typically admin)
- `403` for insufficient permission

### 14) Get my bookings

- `GET /api/bookings/my`

Expected:
- `200 OK`
- returns bookings for logged-in user session

### 15) Cancel booking

- `DELETE /api/bookings/{id}`

Expected:
- `200 OK` if found + permitted
- `404` if not found

## Quick curl Smoke Test

```bash
BASE="http://localhost:8081"

# Register
curl -i -c cookie.txt -b cookie.txt -X POST "$BASE/api/auth/register" \
  -H 'Content-Type: application/json' \
  -d '{"username":"flow_user_1","password":"Password@123","email":"flow_user_1@example.com","fullName":"Flow User","role":"CUSTOMER"}'

# Login
curl -i -c cookie.txt -b cookie.txt -X POST "$BASE/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"flow_user_1","password":"Password@123"}'

# Public endpoint
curl -i -c cookie.txt -b cookie.txt "$BASE/api/flights"

# Protected endpoint
curl -i -c cookie.txt -b cookie.txt "$BASE/api/bookings/my"

# Logout
curl -i -c cookie.txt -b cookie.txt -X POST "$BASE/api/auth/logout"
```

## Detailed Fix Log

For the full chronological debug/fix history, see:

- `/Users/ankitanand/airline-reservation/BACKEND_FIXES_AND_API_TESTING.md`

## Notes

- `fullName` is currently accepted in register payload but not persisted in the `users` table yet.
- `GET /api/flights/search` accepts `date` param in route, but date filtering is not currently applied in facade logic.
