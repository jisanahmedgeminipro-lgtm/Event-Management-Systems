# ER Diagram — Shrabon Decorator & Event Management

The diagram below (Mermaid ERD) describes the normalized relational model.
View it on any Mermaid-compatible renderer (GitHub renders it automatically).

```mermaid
erDiagram
    USERS ||--o| CLIENTS : "1:1 profile"
    USERS ||--o| STAFF : "1:1 profile"

    EVENT_CATEGORIES ||--o{ PACKAGES : categorizes
    EVENT_CATEGORIES ||--o{ BOOKINGS : "event type"

    PACKAGES ||--o{ PACKAGE_IMAGES : has
    PACKAGES ||--o{ PACKAGE_FEATURES : has
    PACKAGES ||--o{ BOOKINGS : "selected in"

    CLIENTS ||--o{ BOOKINGS : places
    CLIENTS ||--o{ REVIEWS : writes

    BOOKINGS ||--|| PAYMENTS : "has aggregate"
    BOOKINGS ||--o{ BOOKING_CUSTOMIZATIONS : includes
    BOOKINGS ||--o{ TASKS : "generates"
    BOOKINGS ||--o{ REVIEWS : "reviewed by"
    BOOKINGS }o--o{ STAFF : "assigned (booking_staff)"

    CUSTOMIZATION_ITEMS ||--o{ BOOKING_CUSTOMIZATIONS : "chosen as"

    PAYMENTS ||--o{ PAYMENT_TRANSACTIONS : "records"

    STAFF ||--o{ TASKS : "responsible for"

    USERS {
        bigint id PK
        string full_name
        string email UK
        string phone
        string password
        string role "ADMIN|STAFF|CLIENT"
        boolean enabled
        string profile_image
        datetime created_at
    }
    CLIENTS {
        bigint id PK
        bigint user_id FK,UK
        string address
        string nid
        string city
    }
    STAFF {
        bigint id PK
        bigint user_id FK,UK
        string position
        decimal salary
        string skills
        string availability
        date join_date
    }
    EVENT_CATEGORIES {
        bigint id PK
        string name UK
        string description
        boolean active
    }
    PACKAGES {
        bigint id PK
        string name
        bigint category_id FK
        decimal base_price
        decimal discount_percent
        int guest_capacity
        boolean featured
        boolean active
    }
    CUSTOMIZATION_ITEMS {
        bigint id PK
        string name
        string type "DECORATION|FOOD|PHOTOGRAPHY|..."
        decimal price
        string unit
        boolean active
    }
    BOOKINGS {
        bigint id PK
        string booking_reference UK
        bigint client_id FK
        bigint package_id FK
        bigint category_id FK
        date event_date
        time event_time
        string venue
        int guest_count
        decimal total_amount
        string status
    }
    BOOKING_CUSTOMIZATIONS {
        bigint id PK
        bigint booking_id FK
        bigint item_id FK
        int quantity
        decimal line_total
    }
    PAYMENTS {
        bigint id PK
        bigint booking_id FK,UK
        decimal total_amount
        decimal paid_amount
        decimal due_amount
        string status "PENDING|PARTIAL|PAID"
    }
    PAYMENT_TRANSACTIONS {
        bigint id PK
        bigint payment_id FK
        decimal amount
        string method
        datetime paid_at
    }
    VENDORS {
        bigint id PK
        string name
        string category
        string phone
    }
    REVIEWS {
        bigint id PK
        bigint client_id FK
        bigint booking_id FK
        int rating "1..5"
        string comment
        boolean approved
    }
    TASKS {
        bigint id PK
        string title
        bigint booking_id FK
        bigint assigned_staff_id FK
        date deadline
        string priority
        string status
    }
    CONTACT_MESSAGES {
        bigint id PK
        string name
        string email
        string message
        boolean handled
    }
    GALLERY_ITEMS {
        bigint id PK
        string image_url
        string category
    }
    WEBSITE_CONTENT {
        bigint id PK
        string content_key UK
        text content_value
    }
```

## Relationship summary

| Relationship | Type | Notes |
|---|---|---|
| User → Client | 1:1 | A CLIENT user has one client profile |
| User → Staff | 1:1 | A STAFF user has one staff profile |
| EventCategory → Package | 1:N | Each package belongs to one category |
| EventCategory → Booking | 1:N | Each booking references an event type |
| Package → Booking | 1:N | A booking may be based on a package |
| Client → Booking | 1:N | A client places many bookings |
| Booking → Payment | 1:1 | One aggregate payment per booking |
| Payment → PaymentTransaction | 1:N | Each part-payment is a transaction (receipt) |
| Booking → BookingCustomization | 1:N | Custom builder line items |
| CustomizationItem → BookingCustomization | 1:N | Catalog item chosen in bookings |
| Booking ↔ Staff | M:N | `booking_staff` join table |
| Staff → Task | 1:N | Tasks assigned to staff |
| Booking → Task | 1:N | Tasks tied to a booking |
| Client → Review | 1:N | Clients leave reviews |
