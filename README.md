# Global Booking System

## Project Overview
A production‑ready backend service for a global live‑learning platform. It manages **courses**, **offerings (sections)**, **sessions**, **parents**, and **bookings**. Teachers create offerings in their own timezone; parents/students view schedules in their local timezone.

## Tech Stack
- **Java 21**
- **Spring Boot 3.3** (Web, Data JPA, Validation)
- **MySQL 8** (JDBC with `allowPublicKeyRetrieval=true`)
- **Maven** (or the Maven wrapper `./mvnw`)
- **Postman** collection for API testing

## Setup Instructions
1. **Clone / initialise** the repo and `cd` into the project root.
2. **Create MySQL database** `booking_system` and run the supplied `schema.sql` (and optional `data.sql`).
3. Add your DB credentials to `src/main/resources/application.properties`.
4. Build & run:
   ```bash
   ./mvnw clean package
   java -jar target/booking-0.0.1-SNAPSHOT.jar
   ```
5. The API will be available at `http://localhost:8080/api`.

## Environment Variables (application.properties)
```
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=none
```

## API Documentation
All endpoints are under the `/api` base path.

### Parent (Student) Endpoints (`/api/parents`)
| Method | URL | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/parents` | Get all parents (used by UI) | – |
| `GET` | `/api/parents/offerings` | List all available offerings (optionally convert times with `?timezone=ZoneId`) | – |
| `POST` | `/api/parents/bookings` | Book a parent into an offering | `{"parentId":Long,"offeringId":Long}` |
| `GET` | `/api/parents/{parentId}/bookings` | Get bookings for a specific parent (optional `timezone` query) | – |

### Teacher (Creator) Endpoints (`/api/teachers`)
| Method | URL | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/teachers/offerings` | Create a new offering for a course | `CreateOfferingRequest` (courseId, teacherId, name, timezone, maxCapacity) |
| `POST` | `/api/teachers/offerings/{offeringId}/sessions` | Add one or more sessions to an existing offering | List of `SessionTimeRequest` (startTime, endTime) |
| `GET` | `/api/teachers/{teacherId}/offerings` | Get all offerings created by a specific teacher (optional `timezone`) | – |

> **Note**: All date‑time strings are ISO‑8601 and are interpreted in the offering’s timezone, then stored as UTC.

## Database Schema Overview
```sql
-- see src/main/resources/schema.sql for full definitions
CREATE TABLE course ( id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255) NOT NULL, description TEXT );
CREATE TABLE offering ( id BIGINT AUTO_INCREMENT PRIMARY KEY, course_id BIGINT NOT NULL, teacher_id BIGINT, name VARCHAR(255) NOT NULL, timezone VARCHAR(64) NOT NULL, max_capacity INT NOT NULL, FOREIGN KEY (course_id) REFERENCES course(id) );
CREATE TABLE session ( id BIGINT AUTO_INCREMENT PRIMARY KEY, offering_id BIGINT NOT NULL, start_time TIMESTAMP NOT NULL, end_time TIMESTAMP NOT NULL, capacity INT NOT NULL, FOREIGN KEY (offering_id) REFERENCES offering(id) );
CREATE TABLE parent ( id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) UNIQUE NOT NULL );
CREATE TABLE booking ( id BIGINT AUTO_INCREMENT PRIMARY KEY, parent_id BIGINT NOT NULL, session_id BIGINT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (parent_id) REFERENCES parent(id), FOREIGN KEY (session_id) REFERENCES session(id), UNIQUE (parent_id, session_id) );
```

## Assumptions
- Teachers provide their timezone when creating an offering; all times are stored in UTC.
- Offerings have a fixed `maxCapacity`; bookings beyond that are rejected.
- Simple parent authentication is out of scope.

## Concurrency Handling
- **Pessimistic write locks** (`SELECT … FOR UPDATE`) on `session` rows during a booking transaction to avoid over‑booking.
- Service methods are annotated with `@Transactional` for atomicity.

## Timezone Handling
- Incoming times are parsed with the offering’s `timezone` and converted to `Instant` (UTC) for persistence.
- Responses include a `full` flag (`currentBookingsCount >= maxCapacity`).
- UI can request times in any timezone via the optional `timezone` query parameter.

## Running Locally
```bash
# 1. Start MySQL and create the DB
# 2. Apply schema.sql
# 3. Set DB credentials in application.properties
# 4. Build & run the app
./mvnw spring-boot:run
```
Open `http://localhost:8080` (static UI) or use the Postman collection (`postman_collection.json`).

---
*Feel free to adjust wording or add more details as your project evolves.*
