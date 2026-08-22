# Event Booking API --- Setup Guide

## 1. Overview

This guide explains how to configure and run the **Event Booking API**
locally.

The application uses Java 17, Spring Boot, Maven, MySQL, Spring Data JPA
/ Hibernate, Spring Security, JWT Authentication, Log4j2 and Swagger /
OpenAPI.

``` mermaid
flowchart LR
    A[Java 17] --> B[Spring Boot API] --> C[Spring Data JPA] --> D[MySQL]
    B --> E[Spring Security] --> F[JWT]
    B --> G[Swagger / OpenAPI]
```

## 2. Prerequisites

Install Java 17, MySQL Server and Git. The project includes the Maven
Wrapper, so a separate Maven installation is not required.

``` bash
java -version
```

## 3. Clone the Project

``` bash
git clone <repository-url>
cd event-booking-api
```

## 4. Application Profiles

``` mermaid
flowchart TD
    A[Spring Boot Application] --> B[dev] --> C[Local Development]
    A --> D[test] --> E[Automated Testing]
    A --> F[prod] --> G[Production]
```

Default profile:

``` properties
spring.profiles.active=dev
```

-   **dev:** `event_booking_db`, Hibernate `update`, SQL logging
    enabled, JWT secret from environment.
-   **test:** `event_booking_test_db`, Hibernate `update`, SQL
    initialization enabled, dedicated test JWT secret.
-   **prod:** database credentials and JWT secret from environment,
    Hibernate `validate`, SQL logging disabled.

## 5. Development Database

The development profile connects to MySQL on `localhost:3306` and uses
`event_booking_db`.

``` properties
createDatabaseIfNotExist=true
```

MySQL can create the database automatically if the configured user has
the required permissions.

``` mermaid
flowchart LR
    A[Spring Boot] --> B[JDBC] --> C[MySQL] --> D[(event_booking_db)]
```

## 6. Environment Variables

Recommended variables:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Production configuration:

``` properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

The same environment-variable approach is recommended for local database
credentials.

## 7. JWT Configuration

``` properties
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

The expiration is **24 hours**.

Windows PowerShell:

``` powershell
$env:JWT_SECRET="your-secure-secret-key"
echo $env:JWT_SECRET
```

Do not commit the real JWT secret to Git.

## 8. Production Configuration

Production expects `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and
`JWT_SECRET`.

``` text
DB_URL=jdbc:mysql://<host>:3306/<database>
DB_USERNAME=<username>
DB_PASSWORD=<password>
JWT_SECRET=<secure-secret>
```

``` properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

Hibernate validates the existing schema instead of modifying it
automatically.

## 9. Build the Project

Windows:

``` powershell
.\mvnw clean compile
```

macOS / Linux:

``` bash
./mvnw clean compile
```

Expected result:

``` text
BUILD SUCCESS
```

## 10. Run Tests

The test profile uses the MySQL database `event_booking_test_db`.

Windows:

``` powershell
.\mvnw clean test
```

macOS / Linux:

``` bash
./mvnw clean test
```

Test configuration includes:

``` properties
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

MySQL must be running because repository tests use the configured test
database.

``` mermaid
flowchart LR
    A[Maven Test] --> B[Test Profile] --> C[Spring Tests] --> D[(event_booking_test_db)] --> E[Test Result]
```

## 11. Run the Application

Windows:

``` powershell
.\mvnw spring-boot:run
```

macOS / Linux:

``` bash
./mvnw spring-boot:run
```

The application runs on port `8080`.

## 12. Verify Application Startup

``` mermaid
flowchart TD
    A[Start Application] --> B{Java 17?}
    B -->|No| X[Startup Error]
    B -->|Yes| C{MySQL Running?}
    C -->|No| X
    C -->|Yes| D{Database Accessible?}
    D -->|No| X
    D -->|Yes| E{JWT_SECRET Available?}
    E -->|No| X
    E -->|Yes| F[Spring Context Starts] --> G[API Ready]
```

Main requirements: Java 17, running MySQL, valid database configuration
and `JWT_SECRET`.

## 13. Swagger / OpenAPI

Swagger UI:

``` text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

``` text
http://localhost:8080/v3/api-docs
```

## 14. Authentication Flow

``` mermaid
flowchart LR
    A[Register / Login] --> B[JWT Token] --> C[Authorization Header] --> D[Protected Endpoint]
```

``` text
Authorization: Bearer <JWT_TOKEN>
```

## 15. Recommended Local Setup Flow

``` mermaid
flowchart TD
    A[Clone Repository] --> B[Verify Java 17] --> C[Start MySQL] --> D[Configure Database]
    D --> E[Set JWT_SECRET] --> F[Compile Project] --> G[Run Tests]
    G --> H[Start Application] --> I[Open Swagger] --> J[Register / Login] --> K[Test API]
```

## 16. Quick Start

``` powershell
$env:JWT_SECRET="your-secure-secret-key"
.\mvnw clean compile
.\mvnw clean test
.\mvnw spring-boot:run
```

Application: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 17. Configuration Summary

  ----------------------------------------------------------------------------------
  Configuration     Development          Test                      Production
  ----------------- -------------------- ------------------------- -----------------
  Profile           `dev`                `test`                    `prod`

  Database          `event_booking_db`   `event_booking_test_db`   Environment

  MySQL             Required             Required                  Required

  Hibernate DDL     `update`             `update`                  `validate`

  Show SQL          Yes                  No                        No

  JWT Secret        Environment          Test value                Environment

  DB Credentials    Local configuration  Local test configuration  Environment

  Port              `8080`               Spring test context       `8080` unless
                                                                   overridden
  ----------------------------------------------------------------------------------

## 18. Security Recommendation

Do not commit database passwords, JWT secrets or production credentials.
Prefer environment variables for sensitive configuration.

``` text
Environment Variables
        ↓
Spring Configuration
        ↓
Application
```

## 19. Setup Summary

``` mermaid
flowchart LR
    A[Configure] --> B[Build] --> C[Test] --> D[Run]
```

1.  Start MySQL.
2.  Configure database credentials.
3.  Set `JWT_SECRET`.
4.  Compile the project.
5.  Run tests.
6.  Start Spring Boot.
7.  Open Swagger.
8.  Authenticate and test the API.

------------------------------------------------------------------------

**Next:** `10-API-USAGE.md`
