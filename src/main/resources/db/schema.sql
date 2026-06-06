-- =============================================================================
--  Shrabon Decorator & Event Management - MySQL Database Schema
--  Engine: InnoDB | Charset: utf8mb4
--  NOTE: Hibernate (ddl-auto=update) will create/maintain these tables at
--        runtime. This script is provided as authoritative documentation and
--        can be executed manually to provision the schema ahead of time.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS shrabon_events
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shrabon_events;

-- -----------------------------------------------------------------------------
-- USERS (authentication & identity for ADMIN / STAFF / CLIENT)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(120)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    phone           VARCHAR(30),
    password        VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL,         -- ADMIN | STAFF | CLIENT
    enabled         BOOLEAN       NOT NULL DEFAULT TRUE,
    profile_image   VARCHAR(255),
    created_at      DATETIME      NOT NULL,
    updated_at      DATETIME,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN','STAFF','CLIENT'))
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- CLIENTS (1:1 profile extension of a CLIENT user)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clients (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    address         VARCHAR(255),
    nid             VARCHAR(40),
    city            VARCHAR(80),
    created_at      DATETIME NOT NULL,
    CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- STAFF (1:1 profile extension of a STAFF user)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS staff (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    position        VARCHAR(100),
    salary          DECIMAL(12,2) DEFAULT 0,
    skills          VARCHAR(500),
    availability    VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE | BUSY | ON_LEAVE
    join_date       DATE,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- EVENT CATEGORIES (Wedding, Reception, Corporate, ... + custom, unlimited)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS event_categories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120) NOT NULL UNIQUE,
    description     VARCHAR(500),
    icon            VARCHAR(80),
    active          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- PACKAGES
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS packages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    category_id     BIGINT,
    description     TEXT,
    base_price      DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    decoration      BOOLEAN NOT NULL DEFAULT FALSE,
    catering        BOOLEAN NOT NULL DEFAULT FALSE,
    photography     BOOLEAN NOT NULL DEFAULT FALSE,
    videography     BOOLEAN NOT NULL DEFAULT FALSE,
    lighting        BOOLEAN NOT NULL DEFAULT FALSE,
    sound_system    BOOLEAN NOT NULL DEFAULT FALSE,
    stage_setup     BOOLEAN NOT NULL DEFAULT FALSE,
    guest_capacity  INT NOT NULL DEFAULT 0,
    featured        BOOLEAN NOT NULL DEFAULT FALSE,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME NOT NULL,
    CONSTRAINT fk_package_category FOREIGN KEY (category_id)
        REFERENCES event_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Package feature bullet points (element collection)
CREATE TABLE IF NOT EXISTS package_features (
    package_id      BIGINT NOT NULL,
    feature         VARCHAR(255) NOT NULL,
    CONSTRAINT fk_pkg_feature FOREIGN KEY (package_id)
        REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Package images
CREATE TABLE IF NOT EXISTS package_images (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id      BIGINT NOT NULL,
    image_url       VARCHAR(255) NOT NULL,
    CONSTRAINT fk_pkg_image FOREIGN KEY (package_id)
        REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- CUSTOMIZATION ITEMS (catalog used by the Custom Package Builder)
-- type: DECORATION | FOOD | PHOTOGRAPHY | VIDEOGRAPHY | LIGHTING | SOUND | STAGE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customization_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    description     VARCHAR(500),
    price           DECIMAL(12,2) NOT NULL DEFAULT 0,
    unit            VARCHAR(40) DEFAULT 'package',  -- e.g. per plate, fixed, per hour
    active          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- BOOKINGS
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference   VARCHAR(30) NOT NULL UNIQUE,
    client_id           BIGINT NOT NULL,
    package_id          BIGINT,
    category_id         BIGINT,
    event_date          DATE NOT NULL,
    event_time          TIME,
    venue               VARCHAR(255) NOT NULL,
    guest_count         INT NOT NULL DEFAULT 0,
    special_requirements TEXT,
    base_amount         DECIMAL(12,2) NOT NULL DEFAULT 0,
    additional_amount   DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount     DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount        DECIMAL(12,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING|CONFIRMED|IN_PROGRESS|COMPLETED|CANCELLED
    created_at          DATETIME NOT NULL,
    updated_at          DATETIME,
    CONSTRAINT fk_booking_client   FOREIGN KEY (client_id)  REFERENCES clients(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_package  FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE SET NULL,
    CONSTRAINT fk_booking_category FOREIGN KEY (category_id) REFERENCES event_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Selected customization items per booking (custom package builder result)
CREATE TABLE IF NOT EXISTS booking_customizations (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id          BIGINT NOT NULL,
    item_id             BIGINT NOT NULL,
    quantity            INT NOT NULL DEFAULT 1,
    line_total          DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_bc_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_bc_item    FOREIGN KEY (item_id)    REFERENCES customization_items(id)
) ENGINE=InnoDB;

-- Staff assigned to a booking (many-to-many)
CREATE TABLE IF NOT EXISTS booking_staff (
    booking_id          BIGINT NOT NULL,
    staff_id            BIGINT NOT NULL,
    PRIMARY KEY (booking_id, staff_id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_bs_staff   FOREIGN KEY (staff_id)   REFERENCES staff(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- PAYMENTS (one per booking - aggregate) + transactions (history/receipts)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL UNIQUE,
    total_amount    DECIMAL(12,2) NOT NULL DEFAULT 0,
    paid_amount     DECIMAL(12,2) NOT NULL DEFAULT 0,
    due_amount      DECIMAL(12,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING | PARTIAL | PAID
    updated_at      DATETIME,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payment_transactions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id      BIGINT NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    method          VARCHAR(40) NOT NULL,           -- CASH | BKASH | NAGAD | CARD | BANK
    reference_no    VARCHAR(80),
    note            VARCHAR(255),
    paid_at         DATETIME NOT NULL,
    CONSTRAINT fk_txn_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- VENDORS
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vendors (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    category        VARCHAR(60) NOT NULL,           -- DECORATOR | CATERER | PHOTOGRAPHER | ...
    contact_person  VARCHAR(120),
    phone           VARCHAR(30),
    email           VARCHAR(150),
    address         VARCHAR(255),
    service_details VARCHAR(1000),
    active          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- REVIEWS
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id       BIGINT NOT NULL,
    booking_id      BIGINT,
    rating          INT NOT NULL,                    -- 1..5
    comment         VARCHAR(1000),
    approved        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL,
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_review_client  FOREIGN KEY (client_id)  REFERENCES clients(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- TASKS (assigned to staff, optionally tied to a booking)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tasks (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(150) NOT NULL,
    description     VARCHAR(1000),
    booking_id      BIGINT,
    assigned_staff_id BIGINT,
    deadline        DATE,
    priority        VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW | MEDIUM | HIGH
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',-- PENDING | IN_PROGRESS | COMPLETED
    created_at      DATETIME NOT NULL,
    CONSTRAINT fk_task_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_staff   FOREIGN KEY (assigned_staff_id) REFERENCES staff(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- CONTACT MESSAGES (public Contact Us form)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS contact_messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(30),
    subject         VARCHAR(200),
    message         VARCHAR(2000) NOT NULL,
    handled         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- GALLERY
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gallery_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(150),
    image_url       VARCHAR(255) NOT NULL,
    category        VARCHAR(80),
    created_at      DATETIME NOT NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- WEBSITE CONTENT (editable key/value content blocks managed by ADMIN)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS website_content (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_key     VARCHAR(100) NOT NULL UNIQUE,
    content_value   TEXT,
    updated_at      DATETIME
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- Helpful indexes
-- -----------------------------------------------------------------------------
CREATE INDEX idx_booking_date_venue ON bookings(event_date, venue);
CREATE INDEX idx_booking_status     ON bookings(status);
CREATE INDEX idx_task_status        ON tasks(status);
CREATE INDEX idx_review_approved    ON reviews(approved);
