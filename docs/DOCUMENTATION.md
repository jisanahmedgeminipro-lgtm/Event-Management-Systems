# Technical Documentation — Shrabon Decorator & Event Management

A complete, role-based **Event Management & Booking System** for *Shrabon Decorator & Event Management* (Proprietor: **Md. Tara Mia**).

This document explains the architecture, modules, data model, security, key
workflows and configuration of the application, derived directly from the source code.

- **Group / Artifact:** `com.shrabon` / `event-management` `1.0.0`
- **Build artifact:** `shrabon-event-management` (`*.jar`)
- **Base package:** `com.shrabon.eventmanagement`

---

## 1. Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Web / UI | Spring MVC + Thymeleaf (server-side rendering), Bootstrap 5 + Bootstrap Icons (CDN), vanilla JS |
| Security | Spring Security 6 (form login, BCrypt, CSRF, role-based URL authorization), `thymeleaf-extras-springsecurity6` |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 (default/`mysql` profile) · H2 in-memory (`dev` profile) |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Email | Spring Boot Mail (SMTP), asynchronous (`@Async` / `@EnableAsync`) |
| Boilerplate | Lombok (`@Getter`/`@Setter`/`@NoArgsConstructor`) |
| Dev | Spring Boot DevTools |

Entry point: `EventManagementApplication` — annotated with `@SpringBootApplication`
and `@EnableAsync` (enables asynchronous email delivery).

---

## 2. Architecture Overview

The application follows a classic layered **MVC + service** architecture. There is
no REST/JSON API surface for end users (one AJAX JSON endpoint exists for live
price quotes); all pages are rendered server-side with Thymeleaf.

```
Browser (Thymeleaf pages + Bootstrap)
        │  HTTP (form posts, GET navigation)
        ▼
Controllers  ──>  Services (interface + impl)  ──>  Repositories (Spring Data JPA)  ──>  MySQL/H2
        │                     │
        │                     └──> EmailService (@Async SMTP notifications)
        ▼
Thymeleaf templates  <── DTOs / entities (model attributes)
```

### Package layout

```
com.shrabon.eventmanagement
├── EventManagementApplication.java      Spring Boot entry point (@EnableAsync)
├── config/                              SecurityConfig, WebConfig, DataInitializer
├── controller/                          public + role controllers, advice, exception handler
│   ├── admin/                           15 admin controllers (/admin/**)
│   ├── client/                          ClientController (/client/**)
│   └── staff/                           StaffController (/staff/**)
├── dto/                                 form-backing + view objects
├── exception/                           BookingConflictException, ResourceNotFoundException
├── model/                               JPA entities
│   └── enums/                           Role, BookingStatus, PaymentStatus, ...
├── repository/                          Spring Data JPA repositories
├── security/                            CustomUserDetails(+Service), LoginSuccessHandler, SecurityUtils
└── service/                             service interfaces
    └── impl/                            service implementations

src/main/resources
├── templates/      Thymeleaf views (fragments, public, admin, staff, client, error)
├── static/         css/style.css, js/main.js, images
├── db/schema.sql   MySQL schema (manual provisioning / documentation)
└── application*.properties
```

---

## 3. Domain Model (Entities)

All entities live in `com.shrabon.eventmanagement.model`. IDs are
`GenerationType.IDENTITY` (auto-increment). Audit timestamps are managed by
JPA lifecycle callbacks (`@PrePersist` / `@PreUpdate`).

| Entity | Table | Purpose / key fields |
|--------|-------|----------------------|
| `User` | `users` | Identity & auth for every user. `fullName`, unique `email`, `phone`, BCrypt `password`, `role` (enum), `enabled`, `profileImage`, `createdAt/updatedAt`. |
| `Client` | `clients` | 1:1 profile for CLIENT users. `user` (FK, unique), `address`, `city`, `nid`. |
| `Staff` | `staff` | 1:1 profile for STAFF users. `user` (FK, unique), `position`, `salary`, `skills`, `availability` (enum), `joinDate`. |
| `EventCategory` | `event_categories` | Event type (Wedding, Reception, Corporate, …). Unique `name`, `description`, `icon`, `active`. |
| `EventPackage` | `packages` | Predefined package (named `EventPackage` because `package` is reserved). `name`, `category` (FK), `basePrice`, `discountPercent`, inclusion booleans (decoration/catering/photography/videography/lighting/soundSystem/stageSetup), `guestCapacity`, `featured`, `active`, `features` (element collection → `package_features`), `images` (1:N). Transient helpers: `getFinalPrice()`, `getDiscountAmount()`, `getCoverImage()`. |
| `PackageImage` | `package_images` | Image URL belonging to a package (N:1). |
| `CustomizationItem` | `customization_items` | Catalog add-on for the Custom Package Builder. `name`, `type` (enum), `description`, `price`, `unit` (e.g. "package"/"plate"), `active`. |
| `Booking` | `bookings` | Central event booking. Unique `bookingReference`, `client` (FK), optional `eventPackage` & `category`, `eventDate`, `eventTime`, `venue`, `guestCount`, `specialRequirements`, amounts (`baseAmount`, `additionalAmount`, `discountAmount`, `totalAmount`), `status` (enum), `customizations` (1:N), `payment` (1:1), `assignedStaff` (M:N via `booking_staff`). Indexed on `(event_date, venue)` and `status`. |
| `BookingCustomization` | `booking_customizations` | Line item linking a booking to a `CustomizationItem` with `quantity` and `lineTotal`. |
| `Payment` | `payments` | Aggregate payment per booking (1:1, unique `booking_id`). `totalAmount`, `paidAmount`, `dueAmount`, `status` (enum), `transactions` (1:N). `recalculate()` derives due amount + status. |
| `PaymentTransaction` | `payment_transactions` | A single part-payment (receipt entry). `amount`, `method` (enum), `referenceNo`, `note`, `paidAt`. |
| `Task` | `tasks` | Work item linked to a `booking` and `assignedStaff`. `title`, `description`, `deadline`, `priority` (enum), `status` (enum). Indexed on `status`. |
| `Vendor` | `vendors` | External supplier. `name`, `category` (enum), `contactPerson`, `phone`, `email`, `address`, `serviceDetails`, `active`. |
| `Review` | `reviews` | Client review. `client` (FK), optional `booking`, `rating` (1–5), `comment`, `approved` (moderation flag). Indexed on `approved`. |
| `ContactMessage` | `contact_messages` | Public contact form submission. `name`, `email`, `phone`, `subject`, `message`, `handled`. |
| `GalleryItem` | `gallery_items` | Public gallery image. `title`, `imageUrl`, `category`. |
| `WebsiteContent` | `website_content` | Editable key/value content blocks for the public site. Unique `contentKey`, `contentValue`. |

### Enums (`model.enums`)

Each enum carries a human-friendly `label` accessor used in templates/emails.

| Enum | Values |
|------|--------|
| `Role` | ADMIN, STAFF, CLIENT |
| `BookingStatus` | PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED |
| `PaymentStatus` | PENDING, PARTIAL, PAID |
| `PaymentMethod` | CASH, BKASH, NAGAD, ROCKET, CARD, BANK |
| `CustomizationType` | DECORATION, FOOD, PHOTOGRAPHY, VIDEOGRAPHY, LIGHTING, SOUND, STAGE |
| `TaskStatus` | PENDING, IN_PROGRESS, COMPLETED |
| `TaskPriority` | LOW, MEDIUM, HIGH |
| `StaffAvailability` | AVAILABLE, BUSY, ON_LEAVE |
| `VendorCategory` | DECORATOR, CATERER, PHOTOGRAPHER, VIDEOGRAPHER, SOUND_PROVIDER, LIGHTING_PROVIDER, TRANSPORT_PROVIDER, OTHER |

> Relationship diagram: see [`docs/ER-DIAGRAM.md`](./ER-DIAGRAM.md).

---

## 4. Persistence Layer (Repositories)

All repositories extend `JpaRepository`. Notable custom methods:

| Repository | Highlights |
|------------|-----------|
| `UserRepository` | `findByEmail`, `existsByEmail`, `findByRole`, `countByRole` |
| `ClientRepository` | `findByUserId`, `findByUserEmail`, `search(q)` (name/email/phone/city LIKE) |
| `StaffRepository` | `findByUserId`, `findByUserEmail`, `findByAvailability` |
| `EventCategoryRepository` | `findByName`, `existsByName`, `findByActiveTrue` |
| `EventPackageRepository` | `findByActiveTrue`, `findByActiveTrueAndFeaturedTrue`, `findByCategoryId` |
| `CustomizationItemRepository` | `findByActiveTrue`, `findByTypeAndActiveTrue` |
| `BookingRepository` | `findByBookingReference`, `findByClientIdOrderByCreatedAtDesc`, `findByStatusOrderByEventDateAsc`, `countByStatus`, **`existsVenueConflict(venue, date, excludeId)`** (double-booking guard), `findUpcoming(fromDate)`, `findByAssignedStaffId`, revenue aggregates (`sumCollectedRevenue`, `sumPendingPayments`, `sumContractedValue`), `findByEventDateBetween...` |
| `PaymentRepository` | `findByBookingId`, `findByStatus`, `findByStatusIn` |
| `ReviewRepository` | `findByApprovedTrueOrderByCreatedAtDesc`, `findByClientId...`, `averageApprovedRating`, `countByApprovedTrue` |
| `TaskRepository` | `findByAssignedStaffIdOrderByDeadlineAsc`, `findByAssignedStaffIdAndStatus`, `countByAssignedStaffIdAndStatus` |
| `ContactMessageRepository` | `findAllByOrderByCreatedAtDesc`, `countByHandledFalse` |
| `GalleryItemRepository` | `findAllByOrderByCreatedAtDesc`, paged `findByOrderByCreatedAtDesc` |
| `VendorRepository` | `findByActiveTrue`, `findByCategory` |
| `WebsiteContentRepository` | `findByContentKey` |

---

## 5. Service Layer

Each service is an interface in `service/` with an implementation in `service/impl/`.
Implementations are `@Transactional(readOnly = true)` by default, with write
methods marked `@Transactional`.

| Service | Responsibility |
|---------|----------------|
| `AuthService` | `registerClient(form)` — validates unique email + password match, creates `User` (role CLIENT) + `Client` profile, hashes password. `emailExists`. |
| `BookingService` | Create booking (with venue-conflict check + auto pricing + customization line items + payment record), lookups by id/reference/client/staff, upcoming list, status updates, staff assignment, venue availability, status counts. |
| `CustomizationService` | CRUD for customization items, `findActiveGroupedByType()`, and **`calculateQuote(packageId, itemIds)`** producing a `QuoteResult` breakdown. |
| `PaymentService` | `getOrCreateForBooking`, lookups, **`recordPayment(form)`** — appends a transaction, increments paid amount, recalculates status. |
| `DashboardService` | `getAdminStats()` → `DashboardStats` KPIs (bookings by status, counts, revenue aggregates, average rating, unread messages, upcoming events). |
| `EventCategoryService`, `PackageService`, `StaffService`, `TaskService`, `VendorService`, `ReviewService`, `GalleryService`, `ContactService`, `ClientService`, `WebsiteContentService` | CRUD / domain operations for their respective entities. |
| `EmailService` | Async SMTP notifications (see §8). Not interface-backed. |

### 5.1 Pricing logic (`CustomizationServiceImpl.calculateQuote`)

```
basePrice        = package.basePrice            (0 if no package)
additionalCost   = Σ price of selected active customization items
subtotal         = basePrice + additionalCost
discount         = basePrice × discountPercent / 100   (discount applies to base only)
finalPrice       = subtotal − discount
```

All monetary math uses `BigDecimal` with `RoundingMode.HALF_UP`, scale 2.

### 5.2 Booking creation (`BookingServiceImpl.createBooking`)

1. Resolve the `Client` from the logged-in user id.
2. **Double-booking prevention** — `existsVenueConflict(venue, date)` (case/space-insensitive venue match). On conflict throws `BookingConflictException`.
3. Build the `Booking`, generate a unique reference `SHB-<year>-<6 digits>` (e.g. `SHB-2026-004821`).
4. Attach optional category and package.
5. Compute amounts via `calculateQuote` and persist customization line items.
6. Create the aggregate `Payment` (paid = 0, status PENDING) and cascade-save.

### 5.3 Payment recording (`PaymentServiceImpl.recordPayment`)

- Gets or creates the payment for the booking, re-syncs `totalAmount` from the booking.
- Adds a `PaymentTransaction` (amount, method, reference, note, timestamp).
- Increases `paidAmount`, then `recalculate()` updates `dueAmount` and sets status PENDING / PARTIAL / PAID.

---

## 6. Web / Controller Layer & Route Map

Controllers return Thymeleaf view names. Cross-cutting concerns:

- `GlobalControllerAdvice` (`@ControllerAdvice`) exposes `companyName`, `companyShortName`, `ownerName`, and `currentUserName` to **all** views via `@ModelAttribute`.
- `GlobalExceptionHandler` (`@ControllerAdvice`) maps `ResourceNotFoundException` and `BookingConflictException` to a friendly error page.

### 6.1 Public routes (`PublicController`, `AuthController`) — no auth required

| Method | Path | Action |
|--------|------|--------|
| GET | `/`, `/home` | Home (hero, featured packages, stats, gallery, reviews) |
| GET | `/about` | About us |
| GET | `/services` | Services |
| GET | `/packages` | Package listing |
| GET | `/packages/{id}` | Package detail |
| GET | `/gallery` | Gallery |
| GET | `/reviews` | Approved reviews |
| GET | `/booking` | Booking entry (redirects to login/client flow; optional `packageId`) |
| GET / POST | `/contact` | Contact info / submit contact form |
| GET | `/login` | Login page |
| GET | `/access-denied` | Access denied page |
| GET / POST | `/register` | Client self-registration |

### 6.2 Client portal (`/client/**`, role CLIENT)

| Method | Path | Action |
|--------|------|--------|
| GET | `/client/dashboard` | Client dashboard |
| GET | `/client/packages` | Browse packages |
| GET | `/client/customize` | Custom Package Builder (optional `packageId`) |
| POST | `/client/customize/quote` | **AJAX JSON** live price quote (`@ResponseBody`) |
| GET | `/client/booking/new` | New booking form (optional `packageId`) |
| POST | `/client/booking` | Create booking |
| GET | `/client/bookings` | My bookings |
| GET | `/client/bookings/{id}` | Booking detail / tracking |
| GET | `/client/payments` | Payment status |
| GET / POST | `/client/reviews` | View / submit review |
| GET / POST | `/client/profile` | View / update profile |

### 6.3 Staff portal (`/staff/**`, role STAFF)

| Method | Path | Action |
|--------|------|--------|
| GET | `/staff/dashboard` | Staff dashboard |
| GET | `/staff/events` | Assigned events |
| GET | `/staff/events/{id}` | Event detail |
| GET | `/staff/tasks` | Tasks |
| POST | `/staff/tasks/{id}/status` | Update task status |
| GET | `/staff/schedule` | Schedule |
| GET | `/staff/notifications` | Notifications |

### 6.4 Admin portal (`/admin/**`, role ADMIN)

| Controller | Base path | Routes |
|------------|-----------|--------|
| `AdminDashboardController` | `/admin` | GET `/dashboard` |
| `AdminBookingController` | `/admin/bookings` | GET `/`, GET `/{id}`, POST `/{id}/status`, POST `/{id}/assign` |
| `AdminPackageController` | `/admin/packages` | GET `/`, GET `/new`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminCustomizationController` | `/admin/customizations` | GET `/`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminEventCategoryController` | `/admin/events` | GET `/`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminClientController` | `/admin/clients` | GET `/` (search `q`), GET `/{id}`, POST `/{id}/delete` |
| `AdminStaffController` | `/admin/staff` | GET `/`, GET `/new`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminTaskController` | `/admin/tasks` | GET `/`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminVendorController` | `/admin/vendors` | GET `/`, GET `/{id}/edit`, POST `/save`, POST `/{id}/delete` |
| `AdminPaymentController` | `/admin/payments` | GET `/`, GET `/booking/{bookingId}`, POST `/record`, GET `/invoice/{bookingId}` |
| `AdminReviewController` | `/admin/reviews` | GET `/`, POST `/{id}/approve`, POST `/{id}/unapprove`, POST `/{id}/delete` |
| `AdminContactController` | `/admin/messages` | GET `/`, POST `/{id}/handled`, POST `/{id}/delete` |
| `AdminGalleryController` | `/admin/gallery` | GET `/`, POST `/save`, POST `/{id}/delete` |
| `AdminContentController` | `/admin/content` | GET `/`, POST `/save` (bulk key/value update) |
| `AdminReportController` | `/admin/reports` | GET `/` |

### 6.5 DTOs (`dto/`)

Form-backing and view objects with Bean Validation: `RegisterForm`, `BookingForm`,
`PackageForm`, `CategoryForm`, `CustomizationItemForm`, `PaymentForm`, `ProfileForm`,
`ReviewForm`, `StaffForm`, `TaskForm`, `VendorForm`, `ContactForm`, plus view objects
`DashboardStats` and `QuoteResult` (price breakdown: base/additional/discount/final).

---

## 7. Security

Configured in `config/SecurityConfig`.

- **Password hashing:** `BCryptPasswordEncoder`.
- **Authentication:** `DaoAuthenticationProvider` backed by `CustomUserDetailsService`
  (loads `User` by email) → `CustomUserDetails` (maps role to authority `ROLE_<role>`).
- **Form login:** custom `/login` page; username parameter is `email`, password `password`;
  failure → `/login?error=true`.
- **Role-based URL authorization:**
  - public paths (`/`, `/home`, `/about`, `/services`, `/packages/**`, `/gallery`, `/reviews`, `/contact`, `/login`, `/register/**`, static assets, `/error`) — permit all
  - `/admin/**` → `ROLE_ADMIN`; `/staff/**` → `ROLE_STAFF`; `/client/**` → `ROLE_CLIENT`
  - everything else → authenticated
- **Login redirect** (`LoginSuccessHandler`): ADMIN → `/admin/dashboard`, STAFF → `/staff/dashboard`, CLIENT → `/client/dashboard`.
- **Logout:** `/logout` → `/login?logout=true`, invalidates session, deletes `JSESSIONID`.
- **Access denied page:** `/access-denied`.
- **CSRF:** enabled (Thymeleaf forms include the token); ignored only for `/h2-console/**`.
- **Headers:** frame options `sameOrigin` (so the H2 console renders in dev).
- **Helpers:** `SecurityUtils` exposes `currentUser()`, `currentUserId()`, `currentEmail()`.

---

## 8. Email Notifications (`EmailService`)

Asynchronous SMTP notifications (`@Async`), gracefully degrade when disabled or
no mail sender is available (logs and returns; failures are caught and logged).

Triggers / templates (plain-text `SimpleMailMessage`):

- `bookingStatus(booking)` — booking status change → client.
- `contactAcknowledgement(message)` — auto-reply to a contact form sender.
- `staffAssignment(staffList, booking)` — notifies assigned staff of an event.
- `taskAssignment(task)` — notifies staff of a new task.

Controlled by `app.mail.enabled` and `app.mail.from`, plus standard `spring.mail.*` SMTP settings.

---

## 9. Bootstrap / Seed Data (`DataInitializer`)

Runs on startup via `ApplicationRunner`. **Idempotent** — only seeds when the
`users` table is empty. It creates:

- Default **admin** (from `app.admin.*` properties).
- Two demo **staff** accounts (password `Staff@12345`).
- One demo **client** (`client@shrabonevents.com` / `Client@12345`).
- 19 **event categories** (Wedding, Reception, Gaye Holud, … Custom Event).
- 4 demo **packages**, 11 **customization items**, 4 **vendors**, 8 **gallery items**.
- Default **website content** blocks (hero, about, mission/vision, contact info).

### Default accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@shrabonevents.com` | `Admin@12345` |
| Staff | `shakil@shrabonevents.com` | `Staff@12345` |
| Client | `client@shrabonevents.com` | `Client@12345` |

> Change the admin credentials via `app.admin.*` in `application.properties`.

---

## 10. Configuration & Profiles

Common config in `application.properties`; profile-specific in
`application-mysql.properties` (default) and `application-dev.properties`.

| Key area | Notes |
|----------|-------|
| Active profile | `spring.profiles.active=mysql` (override with `dev` for H2) |
| Thymeleaf | cache off, `classpath:/templates/`, `.html`, UTF-8 |
| File upload | max file 10MB / request 15MB; `app.upload.dir=uploads` served at `/uploads/**` (see `WebConfig`) |
| Server | port `8080`, whitelabel error disabled (custom error page) |
| JPA | `open-in-view=true` (lets Thymeleaf render lazy associations), `ddl-auto=update` |
| Admin bootstrap | `app.admin.email/password/name` |
| Email | `app.mail.enabled`, `app.mail.from`, `spring.mail.*` (Gmail SMTP, STARTTLS) |

**`mysql` profile:** `jdbc:mysql://localhost:3306/shrabon_events` with
`createDatabaseIfNotExist=true`, `serverTimezone=Asia/Dhaka`; Hibernate manages
schema (`ddl-auto=update`); `spring.sql.init.mode=never` (seed handled by Java).

**`dev` profile:** in-memory H2 (`jdbc:h2:mem:shrabon`, MySQL mode),
`ddl-auto=create-drop`, H2 console at `/h2-console`.

> ⚠️ **Security note:** the committed `application.properties` / `application-mysql.properties`
> contain real-looking database and Gmail SMTP credentials (including an app password).
> For any real deployment these should be moved to environment variables / secrets
> and rotated, not stored in version control.

---

## 11. Running the Application

### Prerequisites
- JDK 21, Maven 3.9+, MySQL 8 (for the default profile).

### MySQL (default)
1. Ensure MySQL is running; update `spring.datasource.username/password` to match your environment.
2. Start:
   ```bash
   mvn spring-boot:run
   ```
3. Open <http://localhost:8080>.

The schema is created automatically (`ddl-auto=update`); you may instead run
`src/main/resources/db/schema.sql` manually.

### Without MySQL (in-memory H2)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:shrabon`).

### Package
```bash
mvn clean package        # produces target/shrabon-event-management.jar
java -jar target/shrabon-event-management.jar
```

---

## 12. Key Features Recap

- **Three role-based portals** (Admin / Staff / Client) with separate dashboards, sidebars and permissions.
- **Custom Package Builder** with live AJAX price calculation (base + add-ons − discount).
- **Double-booking prevention** — the same venue + date cannot be booked twice.
- **Payment management** — advance/partial/paid tracking, per-transaction receipts, invoice page.
- **Moderated reviews**, editable **website content**, **gallery**, **vendor** and **task** management.
- **Email notifications** for booking status, contact acknowledgements, staff and task assignments.
- **Reports** dashboard with revenue aggregates and KPIs.

---

## 13. Frontend Templates

Thymeleaf views under `src/main/resources/templates/`, organized by area:

- `fragments/` — reusable head, footer, topbar, alerts, dashboard widgets, and per-role sidebars/navbars.
- `public/` — home, about, services, packages, package-detail, gallery, reviews, contact, login, register, access-denied.
- `client/` — dashboard, packages, customize, bookings, booking-detail, payments, reviews, profile.
- `staff/` — dashboard, events, event-detail, tasks, schedule, notifications.
- `admin/` — dashboard plus subfolders for bookings, packages, customizations, events, clients, staff, tasks, vendors, payments (incl. invoice), reviews, messages, gallery, content, reports.
- `error/custom-error.html` — friendly error page used by `GlobalExceptionHandler`.

Static assets: `static/css/style.css`, `static/js/main.js`. Bootstrap 5 and
Bootstrap Icons are loaded via CDN.
