# Shrabon Decorator & Event Management

A complete, production-quality **Event Management & Booking System** for *Shrabon Decorator & Event Management* (Proprietor: **Md. Tara Mia**).

Built with **Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA · Thymeleaf · Bootstrap 5 · MySQL**.

---

## Features

### Public Website
Home (hero slider, featured packages, statistics, gallery, reviews), About Us (history, mission, vision, management profiles), Services, Packages, Package detail, Gallery, Reviews, Contact form, Login, Register.

### Role-based portals (separate dashboard, sidebar, navigation & permissions)
| Role | Area | Capabilities |
|------|------|--------------|
| **ADMIN** | `/admin/**` | Dashboard, Bookings, Packages, Customization items, Event types, Clients, Staff, Tasks, Vendors, Payments, Reports, Reviews, Messages, Gallery, Website Content |
| **STAFF** | `/staff/**` | Dashboard, Assigned Events, Tasks, Schedule, Notifications |
| **CLIENT** | `/client/**` | Dashboard, Packages, **Custom Package Builder**, Bookings & tracking, Payment status, Reviews, Profile |

### Highlights
- **Custom Package Builder** with live price calculation (base + add-ons − discount) via AJAX.
- **Double-booking prevention** (same venue + same date cannot be booked twice).
- **Payment management**: advance/partial/paid tracking, transactions, invoice & receipt.
- **Role-based authentication** with BCrypt, CSRF protection, login redirect per role.
- Normalized **MySQL schema** (see `src/main/resources/db/schema.sql`) and **ER diagram** (`docs/ER-DIAGRAM.md`).

---

## Tech Stack
- Java 21, Spring Boot 3.3.x
- Spring Web, Security, Data JPA, Validation, Thymeleaf + Spring Security dialect
- MySQL 8 (production) / H2 (optional dev profile)
- Bootstrap 5 + Bootstrap Icons (CDN), vanilla JavaScript

---

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- MySQL 8 running locally

### 1. Configure the database
Default config (`src/main/resources/application-mysql.properties`):
```
jdbc:mysql://localhost:3306/shrabon_events  (auto-created)
username: root
password: root
```
Update the username/password to match your environment. The schema is created
automatically by Hibernate (`ddl-auto=update`). You may also run
`src/main/resources/db/schema.sql` manually.

### 2. Run
```bash
mvn spring-boot:run
```
Then open: http://localhost:8080

### Run without MySQL (in-memory H2)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shrabon`)

---

## Default Accounts (seeded on first run)
| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@shrabonevents.com` | `Admin@12345` |
| Staff | `shakil@shrabonevents.com` | `Staff@12345` |
| Client | `client@shrabonevents.com` | `Client@12345` |

> Change the admin credentials via `app.admin.*` in `application.properties`.

---

## Project Structure
```
src/main/java/com/shrabon/eventmanagement
├── config/         Security, Web, DataInitializer
├── controller/     public, admin, staff, client controllers
├── dto/            form & view objects
├── exception/      custom exceptions + handler
├── model/          JPA entities + enums
├── repository/     Spring Data repositories
├── security/       UserDetails, success handler, utils
└── service/        service interfaces + impl/

src/main/resources
├── templates/      Thymeleaf (fragments, public, admin, staff, client, error)
├── static/         css/style.css, js/main.js
├── db/schema.sql   MySQL schema (documentation/manual provisioning)
└── application*.properties
```

---

## Security Notes
- Passwords hashed with BCrypt.
- CSRF protection enabled (Thymeleaf forms include the token automatically).
- URL authorization strictly segregates `/admin`, `/staff`, `/client` areas.
- Login redirects: ADMIN → `/admin/dashboard`, STAFF → `/staff/dashboard`, CLIENT → `/client/dashboard`.
