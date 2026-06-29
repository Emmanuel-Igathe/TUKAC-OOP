# TUKAC Portal — Web Application Setup Guide

Technical University of Kenya Ability Club — full-stack web portal (Spring Boot + SQLite + Vanilla JS).

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17 or higher | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org/download.cgi |

Verify your installation:
```bash
java -version     # should print 17.x.x
mvn -version      # should print Apache Maven 3.x
```

---

## Running the Application

### 1. Navigate to the backend project

```bash
cd "tukac-portal"
```

> All commands below must be run from inside the `tukac-portal/` directory.

### 2. Build and start the server

```bash
mvn spring-boot:run
```

Maven will download dependencies (~first run only), compile the Java source, and start an embedded Tomcat server.

You should see output like:
```
  .   ____          _
 /\\ / ___'_ __ _ _(_)_ __
( ( )\___ | '_ | '_| | '_ \/ _`)
 \\/  ___)| |_)| | | | | || (_| |
  '\_______|____/_| |_|_| |_\__, |
                              |___/
 :: Spring Boot ::               (v3.2.5)

Database initialized successfully.
Tomcat started on port 8080
Started WebApp in X.XXX seconds
```

### 3. Open in browser

```
http://localhost:8080
```

---

## Default Admin Account

Log in immediately with:

| Field    | Value             |
|----------|-------------------|
| Email    | admin@tukac.com   |
| Password | admin123          |
| Role     | Chairperson (Admin) |

---

## Feature Walkthrough

### Public Pages (no login required)
- **Home** (`/`) — Hero section and about cards
- **Events** (`#/events`) — Live events from the database with RSVP buttons
- **Blog** (`#/blog`) — Live blog posts from the database

### Member Pages (login required)
- **Login** (`#/login`) — Authenticate with email + password
- **Register** (`#/register`) — Create account (requires admin approval)
- **Dashboard** (`#/dashboard`) — Personal stats and upcoming events

### Admin Pages (Chairperson only)
- **Members** (`#/members`) — View all members, approve pending registrations
- **Finance** (`#/finance`) — View income/expense summary, add transactions

---

## API Endpoints Reference

All API calls use `/api` as the base path. Protected routes require `Authorization: Bearer <token>`.

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | None | Login → returns `{ token, user }` |
| POST | `/api/auth/register` | None | Register new member |
| GET | `/api/events/public` | None | List all events |
| POST | `/api/events/{id}/register` | User | RSVP to an event |
| GET | `/api/blog/public` | None | List all blog posts |
| GET | `/api/dashboard/stats` | User | Club statistics |
| GET | `/api/members` | Admin | All members |
| POST | `/api/members/{id}/approve` | Admin | Approve pending member |
| GET | `/api/finance` | Admin | Finance summary + transactions |
| POST | `/api/finance` | Admin | Add a transaction |

---

## Database

The app uses **SQLite** stored at `TUKAC-OOP/tukac.db` (created automatically on first run beside the project root).

The database is initialized with:
- All required tables (users, events, event_registrations, transactions, blog_posts, comments)
- One default admin user (`admin@tukac.com` / `admin123`)

---

## Build Executable JAR

To create a standalone JAR that can be deployed anywhere:

```bash
cd tukac-portal
mvn clean package -DskipTests
java -jar target/tukac-portal-1.0-SNAPSHOT.jar
```

---

## Project Structure

```
TUKAC-OOP/
├── README.md                          ← This file
├── tukac-portal/
│   ├── pom.xml                        ← Maven config (Spring Boot 3.2.5)
│   └── src/main/
│       ├── java/com/tukac/
│       │   ├── WebApp.java            ← Spring Boot entry point
│       │   ├── App.java               ← Original Swing desktop app
│       │   ├── auth/TokenStore.java   ← In-memory Bearer token store
│       │   ├── filter/AuthFilter.java ← Request authentication filter
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── EventController.java
│       │   │   ├── BlogController.java
│       │   │   ├── MemberController.java
│       │   │   ├── FinanceController.java
│       │   │   └── DashboardController.java
│       │   ├── db/Database.java       ← SQLite connection + schema init
│       │   └── models/User.java       ← User domain model
│       └── resources/
│           ├── application.properties ← Server port config
│           └── static/
│               └── index.html         ← Single-page frontend (HTML+CSS+JS)
└── src/main/resources/static/         ← Original frontend files (reference)
```

---

## Troubleshooting

**Port already in use**
```bash
# Change the port in application.properties:
server.port=9090
# Then access: http://localhost:9090
```

**Maven not found**
```bash
# On Windows, add Maven bin to PATH or use the full path:
"C:\Program Files\Apache Maven\bin\mvn" spring-boot:run
```

**Database locked error**
Close any other process using `tukac.db` (e.g., the Swing desktop app) before starting the web server.

**Login says "pending approval"**
Log in as admin (`admin@tukac.com` / `admin123`) → go to Members → click Approve on the pending account.
