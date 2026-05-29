# Global Booking System

## Project Overview
A production‑ready backend service for a global live‑learning platform. It manages **courses**, **offerings (sections)**, **sessions**, **parents**, and **bookings**. Teachers create offerings in their timezone; students/parents view schedules in their local timezone. The system handles concurrency, time‑zone conversion, and provides a dynamic frontend dashboard.

## Tech Stack
- **Java 21**
- **Spring Boot 3.3** (Spring Web, Spring Data JPA, Spring Validation)
- **MySQL 8** (JDBC with `allowPublicKeyRetrieval=true`)
- **Maven** for build management
- **HTML/CSS/JavaScript** (static UI in `src/main/resources/static/`)
- **IntelliJ IDEA** (recommended IDE)

## Setup Instructions
1. **Clone the repository** (or work in the existing scratch directory `C:\Users\HP\.gemini\antigravity\scratch\global-booking-system`).
2. **Configure JDK** – ensure Java 21 is installed and `JAVA_HOME` points to it.
3. **Create a MySQL database** named `booking_system` and a user with appropriate privileges.
4. Update `src/main/resources/application.properties` with your DB credentials.
5. Build the project:
   ```bash
   mvn clean package
   ```
6. Run the application:
   ```bash
   java -jar target/booking-0.0.1-SNAPSHOT.jar
   ```
   The service will start on **http://localhost:8080**.

## Environment Variables Required
| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL (must include `allowPublicKeyRetrieval=true`) | `jdbc:mysql://localhost:3306/booking_db?useSSL=false&allowPublicKeyRetrieval=true` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `booking_user` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `your_password` |
| `SERVER_PORT` *(optional)* | Port for the embedded Tomcat | `8080` |

## API Documentation
### Base URL
`http://localhost:8080/api`

| Endpoint | Method | Description | Request Body | Response |
|---|---|---|---|---|
| `/courses` | GET | List all courses | – | `[{id, name, description}]` |
| `/courses` | POST | Create a new course | `{name, description}` | Created course object |
| `/offerings` | GET | List all offerings (with sessions) | – | `[{id, courseId, name, timezone, sessions[]}]` |
| `/offerings` | POST | Create an offering | `{courseId, name, timezone, sessions[]}` | Created offering |
| `/parents` | GET | List all parents (used by UI) | – | `[{id, name, email}]` |
| `/bookings` | POST | Book a student into a session | `{parentId, offeringId, sessionId}` | Booking confirmation or error |
| `/bookings/{id}` | DELETE | Cancel a booking | – | Success/Failure message |

All dates/times are stored in **UTC** and converted to the caller’s local timezone based on the `timezone` field supplied when creating an offering.

## Database Schema Overview
```sql
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE offering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE TABLE session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offering_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,   -- stored as UTC
    end_time TIMESTAMP NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT fk_offering FOREIGN KEY (offering_id) REFERENCES offering(id)
);

CREATE TABLE parent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES parent(id),
    CONSTRAINT fk_session FOREIGN KEY (session_id) REFERENCES session(id),
    UNIQUE (parent_id, session_id) -- prevent duplicate bookings
);
```

## Assumptions Made
- Teachers create offerings in their native timezone; the `timezone` column on `offering` captures this.
- All timestamps are persisted as UTC (`Instant` in Java) to avoid daylight‑saving issues.
- Capacity limits are enforced per session; over‑booking is prevented via pessimistic write locks.
- Parents are the primary users interacting with the UI; authentication/authorization is out of scope.
- Simple static seed data (`schema.sql` / `data.sql`) is sufficient for demo purposes.

## Concurrency Handling Approach
- **Pessimistic write locking** (`SELECT ... FOR UPDATE`) on `session` rows during booking to avoid race conditions.
- Service methods are annotated with `@Transactional` to ensure atomicity.
- Unique constraint on `(parent_id, session_id)` prevents duplicate bookings.
- Spring Data JPA repositories expose custom methods like `findByIdForUpdate(Long id)`.

## Timezone Handling Approach
- Offerings store a `timezone` string (e.g., `Asia/Kolkata`).
- When creating a session, the input local time is parsed with that `ZoneId` and converted to UTC (`Instant`) for persistence.
- API responses include both UTC timestamps and optional local‑formatted strings for convenience.
- Frontend fetches the `timezone` and performs client‑side formatting when displaying schedules.

## Steps to Run the Application Locally
1. **Start MySQL** and create the `booking_system` database.
2. **Apply schema** – the `schema.sql` file is executed automatically on startup (Spring `spring.datasource.initialize=true`).
3. **Load seed data** – `data.sql` provides sample courses, offerings, sessions, and parents.
4. **Build & run** (see Setup Instructions).
5. Open a browser at `http://localhost:8080/` to view the dynamic dashboard.
6. Use tools like **Postman** or **curl** to interact with the REST API endpoints listed above.

---

