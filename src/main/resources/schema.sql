-- =========================================================================
-- DATABASE SCHEMA DEFINITION
-- This file contains SQL instructions to set up the tables for our system.
-- We drop tables first so that we start with a clean slate on every run.
-- =========================================================================

-- Disable foreign key checks temporarily so we don't get errors while dropping tables
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS offerings;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS parents;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Courses Table: Stores details about the subject taught (e.g. Java Backend)
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

-- 2. Offerings Table: Represents a specific schedule/batch for a course
CREATE TABLE offerings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,       -- e.g., "Saturday Batch" or "Summer Camp"
    timezone VARCHAR(100) NOT NULL,   -- Timezone where the teacher created this offering
    max_capacity INT NOT NULL DEFAULT 10,
    current_bookings_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 3. Sessions Table: Stores the actual meeting times for an offering
-- We store start_time and end_time as TIMESTAMP in UTC.
-- This ensures that they are stored in a standard format regardless of server timezone.
CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offering_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL, -- Stored in UTC
    end_time TIMESTAMP NOT NULL,   -- Stored in UTC
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
);

-- 4. Parents Table: Represents parent profiles
CREATE TABLE parents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    timezone VARCHAR(100) NOT NULL -- Parent's preferred timezone for viewing schedules
);

-- 5. Bookings Table: Tracks which parents have booked which offerings
-- "unique_parent_offering" ensures a parent cannot book the SAME offering twice.
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES parents(id),
    FOREIGN KEY (offering_id) REFERENCES offerings(id),
    CONSTRAINT unique_parent_offering UNIQUE (parent_id, offering_id)
);
