# Environment Setup Instructions

## Prerequisites
- **Java 21** (JDK) – make sure `java -version` prints `21`.
- **Maven** (or use the Maven wrapper `./mvnw`).
- **MySQL 8** (or compatible MariaDB) instance running locally.
- **Git** for version control.

## 1. Clone the repository (or initialise a new one)
```bash
# If the project already exists locally, skip cloning.
git clone <your‑repo‑url> global-booking-system
cd global-booking-system
```
If you are starting from the current scratch directory, initialise:
```bash
git init
```

## 2. Database preparation
1. Create a database (e.g., `booking_system`).
   ```sql
   CREATE DATABASE booking_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Apply the **schema** and **seed data**.
   ```bash
   # The project ships `schema.sql` in src/main/resources.
   mysql -u <user> -p booking_db < src/main/resources/schema.sql
   # (Optional) load sample data if you have a `data.sql` file.
   # mysql -u <user> -p booking_db < src/main/resources/data.sql
   ```

## 3. Configure application properties
Create (or edit) `src/main/resources/application.properties` with your DB credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=none   # schema is managed via schema.sql
```

## 4. Build and run the application
```bash
# Using Maven wrapper (recommended)
./mvnw clean package
java -jar target/booking-0.0.1-SNAPSHOT.jar
```
The service will start on **http://localhost:8080**.

## 5. Verify the API
Open the Swagger UI (if you added the SpringDoc dependency) at:
```
http://localhost:8080/swagger-ui.html
```
Or import the generated **Postman collection** (`postman_collection.json`) into Postman and test the endpoints.

## 6. Add files to Git and push
```bash
git add src/main/resources/schema.sql
git add postman_collection.json
git add ENV_SETUP.md

git commit -m "Add DB schema, Postman collection, and setup instructions"

git remote add origin <your‑remote‑url>   # if not set
git push -u origin main
```

---

