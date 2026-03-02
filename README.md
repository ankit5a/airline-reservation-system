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

| Module             | Responsibility                                                      |
| ------------------ | ------------------------------------------------------------------- |
| `airline-entities` | Domain model: `User`, `Flight`, `Booking`, `Passenger`              |
| `airline-web-dtos` | API contracts: request/response DTO classes                         |
| `airline-web`      | REST controllers, business logic, ACL/CORS filters, routing, config |

## Tech Stack

| Layer            | Technology                          |
| ---------------- | ----------------------------------- |
| Language         | Java 17                             |
| Framework        | Ninja 6.8.0                         |
| DI               | Google Guice + Guice Persist        |
| ORM/JPA          | Hibernate 4.x + `javax.persistence` |
| Database         | PostgreSQL                          |
| JSON             | Jackson                             |
| Password Hashing | BCrypt (`jbcrypt`)                  |
| Logging          | Logback                             |
| Build            | Maven                               |

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL running locally (or reachable)

## Configuration

Runtime files:

- `airline-web/src/main/resources/conf/application.conf`
- `airline-web/src/main/resources/conf/example.application.conf`

Use placeholders in `example.application.conf` and create your local `application.conf` with your own values.

## Database Setup

### 1) Create role/database

```sql
CREATE USER <db_user> WITH PASSWORD '<db_password>';
CREATE DATABASE <db_name> OWNER <db_user>;
```

### 2) Ensure schema permissions (if needed)

If you see permission errors on schema `public`, run:

```sql
GRANT USAGE, CREATE ON SCHEMA public TO <db_user>;
ALTER SCHEMA public OWNER TO <db_user>;
```

> The app uses schema auto-update at startup, so tables are created/updated when DB permissions are correct.

## Build and Run

From repo root:

```bash
mvn clean install
mvn -pl airline-web -DskipTests ninja:run
```

Server URL:

- `http://localhost:8081` (or the port configured in your local `application.conf`)

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

## Authentication Model (Testing)

Login stores session data (`userId`, `userRole`, `authToken`).
Protected endpoints require valid session context.

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
  "username": "user_1",
  "password": "Password@123",
  "email": "user_1@example.com",
  "fullName": "User One",
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
  "username": "user_1",
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
- filtered results

### 7) Create flight (admin)

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

- `200 OK` for admin session
- `401/403` if unauthorized

### 8) Update flight (admin)

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

### 9) Delete flight (admin)

- `DELETE /api/flights/{id}`

Expected:

- `200 OK` if admin + flight exists
- `404` if not found

## Booking Endpoints (Protected)

### 10) Create booking (new passenger)

- `POST /api/bookings`
- Body:

```json
{
  "flightId": 1,
  "newPassenger": {
    "firstName": "User",
    "lastName": "One",
    "passportNumber": "P1234567",
    "email": "passenger@example.com",
    "phone": "+919999999999"
  }
}
```

Expected:

- `200 OK` booking created
- `404` if flight missing
- `400` invalid payload

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

- `200 OK` for roles with `VIEW_ALL_BOOKINGS`
- `403` for insufficient permission

### 14) Get my bookings

- `GET /api/bookings/my`

Expected:

- `200 OK`
- user-scoped bookings

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

## Notes

- `GET /api/flights/search` accepts `date` param in route, but date filtering is not currently applied.
