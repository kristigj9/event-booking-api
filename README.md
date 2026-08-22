# Event Booking API

A REST API for managing events, venues, seats, bookings, payments,
reviews, waitlists and notifications.

The project is built with **Java 17**, **Spring Boot**, **Spring
Security**, **JWT**, **Spring Data JPA**, **MySQL** and **Swagger /
OpenAPI**.

------------------------------------------------------------------------

## Project Overview

The Event Booking API manages the complete event-booking lifecycle, from
authentication and event discovery to seat reservation, booking
confirmation, payment, notifications and reviews.

Main actors:

-   **USER** --- browses events and manages own bookings, payments,
    reviews, waitlist entries and notifications.
-   **ORGANIZER** --- manages events and event-specific seats according
    to ownership rules.
-   **ADMIN** --- manages protected administrative resources and
    operations.

``` mermaid
flowchart LR
    A[User]
    --> B[Authentication]
    --> C[Event]
    --> D[EventSeat]
    --> E[Booking]
    --> F[Payment]
    --> G[Notification]
    --> H[Review]
```

When seats are unavailable, the waitlist extends the normal booking
flow:

``` mermaid
flowchart LR
    A[Event Unavailable]
    --> B[Waitlist]
    --> C[WAITING]
    --> D[NOTIFIED]
    --> E[Notification]
    --> F[Booking Opportunity]
```

------------------------------------------------------------------------

## Architecture

The application follows a layered architecture.

``` mermaid
flowchart LR
    CLIENT[Client]
    --> SEC[Spring Security]
    --> CTRL[Controller]
    --> SERV[Service]
    --> REP[Repository]
    --> DB[(MySQL)]

    SERV --> MAP[Mapper]
```

Main responsibilities:

  Layer        Responsibility
  ------------ ---------------------------------------------
  Controller   Handles HTTP requests and responses
  Service      Contains business logic and ownership rules
  Repository   Handles database access using Spring Data JPA, derived queries, JPQL and native SQL
  Mapper       Converts entities and DTOs
  Security     Handles authentication and authorization
  Database     Stores application data

------------------------------------------------------------------------

## Data Access

The persistence layer uses **Spring Data JPA** and demonstrates the
different database querying approaches required by the project.

### Derived Query Methods

Spring Data JPA derived query methods are used throughout the repository
layer for common database operations.

Examples include:

``` java
findByOrganizerId(...)
findByEventId(...)
findByCategoriesId(...)
existsByUserIdAndEventId(...)
```

Spring Data JPA derives the required query automatically from the method
name.

### JPQL

JPQL is used in `EventRepository` to retrieve events by status.

``` java
@Query("""
        SELECT e
        FROM Event e
        WHERE e.eventStatus = :status
        """)
List<Event> findEventsByStatusJPQL(
        @Param("status") EventStatus status
);
```

JPQL operates on JPA entities and their fields rather than directly on
database table and column names.

### Native SQL

A native SQL query is used in `BookingRepository` to retrieve bookings
belonging to a specific user.

``` java
@Query(
        value = """
                SELECT *
                FROM bookings
                WHERE user_id = :userId
                """,
        nativeQuery = true
)
List<Booking> findBookingsByUserNative(
        @Param("userId") Long userId
);
```

Unlike JPQL, native queries operate directly on database tables and
columns.

The data access layer therefore demonstrates all three querying
approaches:

``` text
Derived Query Methods
        +
JPQL
        +
Native SQL
```



## Core Domains

The project contains the following main modules:

``` text
Auth
User
Category
Venue
Seat
Event
EventSeat
Booking
Payment
Review
Waitlist
Notification
```

The domain model separates a physical `Seat` from an `EventSeat`.

``` mermaid
flowchart LR
    V[Venue] --> S[Seat]
    E[Event] --> ES[EventSeat]
    S --> ES
    ES --> B[Booking]
```

A `Seat` belongs to a venue, while an `EventSeat` represents that seat
for a specific event with event-specific availability, status and price.

------------------------------------------------------------------------

## Main Booking Flow

``` mermaid
flowchart TD
    A[Register / Login]
    --> B[JWT Token]
    --> C[Browse Events]
    --> D[View Event Seats]
    --> E[Select AVAILABLE Seats]
    --> F[Create Booking]
    --> G[Booking PENDING]
    --> H[Seats RESERVED]
    --> I[Confirm Booking]
    --> J[Booking CONFIRMED]
    --> K[Seats SOLD]
    --> L[Create Payment]
    --> M[Payment PENDING]
    --> N[Complete Payment]
    --> O[Payment COMPLETED]
    --> P[Notification]
```

------------------------------------------------------------------------

## Domain Lifecycles

### Booking

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> CONFIRMED
    PENDING --> CANCELLED
    CONFIRMED --> CANCELLED
    CONFIRMED --> COMPLETED
```

### Event Seat

``` mermaid
stateDiagram-v2
    AVAILABLE --> RESERVED: Booking created
    RESERVED --> SOLD: Booking confirmed
    RESERVED --> AVAILABLE: Booking cancelled
```

### Payment

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> COMPLETED
    COMPLETED --> REFUNDED
```

### Notification

``` mermaid
stateDiagram-v2
    [*] --> UNREAD
    UNREAD --> READ
```

Detailed lifecycle rules are documented in
`docs/04-DOMAIN-LIFECYCLE.md`.

------------------------------------------------------------------------

## Security

The API uses **Spring Security + JWT** for stateless authentication.

Protected requests use:

``` text
Authorization: Bearer <JWT_TOKEN>
```

``` mermaid
flowchart TD
    A[Request]
    --> B{Valid JWT?}

    B -->|No| C[401 Unauthorized]
    B -->|Yes| D[Authenticated User]
    --> E{Role Allowed?}

    E -->|No| F[403 Forbidden]
    E -->|Yes| G[Controller]
    --> H[Service]
    --> I[Ownership Validation]
```

Authorization combines:

``` text
Role-Based Authorization
        +
Resource Ownership
```

Main roles:

``` text
USER
ORGANIZER
ADMIN
```

Ownership rules protect resources such as events, bookings, payments,
reviews and notifications.

------------------------------------------------------------------------

## API Documentation

The project uses Swagger / OpenAPI.

After starting the application locally:

``` text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

``` text
http://localhost:8080/v3/api-docs
```

Main API groups include:

``` text
Authentication
Users
Categories
Venues
Seats
Events
Event Seats
Bookings
Payments
Reviews
Waitlists
Notifications
```

------------------------------------------------------------------------

## Testing

The project contains automated tests across the main application layers.

``` mermaid
flowchart TD
    T[Automated Tests]
    --> R[Repository Tests]
    --> S[Service Tests]
    --> C[Controller Tests]
    --> SEC[Security Tests]
```

Tests cover persistence, business rules, state transitions, validation,
ownership, authentication and authorization.

A verified full test execution during development completed with:

``` text
Tests run: 326
Failures: 0
Errors: 0
Skipped: 0
```

Because the test suite can evolve, run the current suite to verify the
latest result:

``` powershell
.\mvnw clean test
```

------------------------------------------------------------------------

## Technology Stack

  Technology           Usage
  -------------------- ---------------------------------
  Java 17              Programming language
  Spring Boot          Application framework
  Spring MVC           REST API
  Spring Data JPA      Persistence
  Hibernate            ORM
  MySQL                Database
  Spring Security      Security
  JWT                  Authentication
  Jakarta Validation   DTO validation
  Maven                Build and dependency management
  Log4j2               Logging
  Swagger / OpenAPI    API documentation
  JUnit 5              Testing
  Mockito              Unit testing
  MockMvc              Controller and security testing

------------------------------------------------------------------------

## Configuration

The project uses separate Spring profiles:

``` text
dev
test
prod
```

Development database:

``` text
event_booking_db
```

Test database:

``` text
event_booking_test_db
```

Sensitive configuration should be provided through environment
variables.

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Do not commit real database passwords, JWT secrets or production
credentials.

------------------------------------------------------------------------

## Quick Start

### Requirements

``` text
Java 17
MySQL Server
Git
```

### Set JWT Secret

Windows PowerShell:

``` powershell
$env:JWT_SECRET="your-secure-secret-key"
```

### Compile

``` powershell
.\mvnw clean compile
```

### Run Tests

``` powershell
.\mvnw clean test
```

### Start Application

``` powershell
.\mvnw spring-boot:run
```

Application:

``` text
http://localhost:8080
```

Swagger:

``` text
http://localhost:8080/swagger-ui/index.html
```

For complete setup instructions, see `docs/09-SETUP.md`.

------------------------------------------------------------------------

## Documentation

Detailed project documentation is available in the `docs` directory:

``` text
docs/
├── 01-PROJECT-OVERVIEW.md
├── 02-ARCHITECTURE.md
├── 03-DATABASE-DESIGN.md
├── 04-DOMAIN-LIFECYCLE.md
├── 05-API-ENDPOINTS.md
├── 06-SECURITY.md
├── 07-BEAN-LIFECYCLE.md
├── 08-TESTING.md
├── 09-SETUP.md
└── 10-API-USAGE.md
```

Each document focuses on one part of the application so that the main
README remains concise and easy to navigate.

------------------------------------------------------------------------

## Key Technical Decisions

The project demonstrates several important backend design decisions:

-   Layered architecture
-   Request and response DTO separation
-   Dedicated mapper layer
-   Layered architecture
-   Request and response DTO separation
-   Dedicated mapper layer
-   Spring Data JPA with derived query methods, JPQL and native SQL
-   JWT-based stateless authentication
-   JWT-based stateless authentication
-   Role-based authorization
-   Resource ownership validation
-   Physical `Seat` and event-specific `EventSeat` separation
-   Controlled booking and payment lifecycles
-   Waitlist and notification integration
-   Global exception handling
-   Transactional business operations
-   Automated layered testing
-   Environment-specific configuration
------------------------------------------------------------------------

## Final Overview

``` mermaid
flowchart TD
    CLIENT[Client]
    --> AUTH[JWT Security]
    --> API[REST Controllers]
    --> SERVICE[Service Layer]

    SERVICE --> MAPPER[Mapper Layer]
    SERVICE --> REPOSITORY[Repository Layer]
    REPOSITORY --> DB[(MySQL)]

    SERVICE --> EVENT[Event]
    EVENT --> SEAT[EventSeat]
    SEAT --> BOOKING[Booking]
    BOOKING --> PAYMENT[Payment]
    BOOKING --> WAITLIST[Waitlist]
    PAYMENT --> NOTIFICATION[Notification]
    WAITLIST --> NOTIFICATION
    BOOKING --> REVIEW[Review]
```

The Event Booking API provides a structured backend for managing event
discovery, venue seating, reservations, payments, waitlists,
notifications and reviews while keeping security and business rules
separated across dedicated application layers.
