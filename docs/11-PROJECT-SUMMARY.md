# Event Booking API --- Project Summary

## 1. Project Goal

The **Event Booking API** is a Spring Boot REST API designed to manage
the complete lifecycle of event booking.

The system supports:

-   User authentication and profile management
-   Role-based access control
-   Event and venue management
-   Physical seats and event-specific seats
-   Booking lifecycle management
-   Payment processing lifecycle
-   Reviews
-   Waitlists
-   Notifications

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

------------------------------------------------------------------------

## 2. Technology Stack

The project is built with:

  Technology           Purpose
  -------------------- ----------------------------------
  Java 17              Programming language
  Spring Boot          Application framework
  Spring MVC           REST API
  Spring Data JPA      Persistence layer
  Hibernate            ORM
  MySQL                Relational database
  Spring Security      Authentication and authorization
  JWT                  Stateless authentication
  Jakarta Validation   Request validation
  Maven                Build and dependency management
  Log4j2               Logging
  Swagger / OpenAPI    API documentation
  JUnit 5              Automated testing
  Mockito              Unit testing and mocks
  MockMvc              Controller and security testing

------------------------------------------------------------------------

## 3. Application Architecture

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

Each layer has a clear responsibility:

  Layer        Responsibility
  ------------ ----------------------------------
  Controller   HTTP requests and responses
  Service      Business logic
  Repository   Database access
  Mapper       DTO and entity conversion
  Security     Authentication and authorization
  Database     Persistent application data

This separation keeps HTTP logic, business rules and persistence
responsibilities independent.

------------------------------------------------------------------------

## 4. Core Domains

The main application domains are:

``` mermaid
flowchart TD
    USER[User]

    USER --> EVENT[Event]
    EVENT --> VENUE[Venue]
    VENUE --> SEAT[Seat]

    EVENT --> ES[EventSeat]
    SEAT --> ES

    USER --> BOOK[Booking]
    EVENT --> BOOK
    ES --> BOOK

    BOOK --> PAY[Payment]
    USER --> REV[Review]
    EVENT --> REV

    USER --> WAIT[Waitlist]
    EVENT --> WAIT

    USER --> NOT[Notification]
```

Main modules:

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

------------------------------------------------------------------------

## 5. Event and Seat Model

The project separates a physical `Seat` from an `EventSeat`.

A physical seat belongs to a venue:

``` mermaid
flowchart LR
    V[Venue]
    --> S[Seat]
```

An `EventSeat` connects that physical seat to a specific event:

``` mermaid
flowchart LR
    E[Event]
    --> ES[EventSeat]
    S[Seat]
    --> ES
```

This allows the same physical venue seat to be reused across different
events while maintaining event-specific:

-   Availability
-   Status
-   Price

Main EventSeat states are:

``` text
AVAILABLE
RESERVED
SOLD
```

------------------------------------------------------------------------

## 6. Main Business Flow

The central business flow connects events, seats, bookings and payments.

``` mermaid
flowchart TD
    A[Browse Event]
    --> B[View Event Seats]
    --> C[Select AVAILABLE Seats]
    --> D[Create Booking]
    --> E[Booking PENDING]
    --> F[Seats RESERVED]
    --> G[Confirm Booking]
    --> H[Booking CONFIRMED]
    --> I[Seats SOLD]
    --> J[Create Payment]
    --> K[Payment PENDING]
    --> L[Complete Payment]
    --> M[Payment COMPLETED]
    --> N[Notification]
```

This flow keeps booking state and seat availability synchronized.

------------------------------------------------------------------------

## 7. Booking Lifecycle

The Booking domain uses controlled state transitions.

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> CONFIRMED
    PENDING --> CANCELLED
    CONFIRMED --> CANCELLED
    CONFIRMED --> COMPLETED
```

Important booking rules include:

-   Selected seats must be available.
-   Duplicate seat selection is rejected.
-   Booking creation reserves seats.
-   Confirmation marks reserved seats as sold.
-   Cancellation releases eligible seats.
-   Available seat counts are synchronized with booking operations.
-   Ownership is validated for protected operations.

------------------------------------------------------------------------

## 8. Payment Lifecycle

Payments are linked to bookings.

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> COMPLETED
    COMPLETED --> REFUNDED
```

Important payment rules include:

-   A booking cannot have duplicate payments.
-   Payment amount is based on selected EventSeat prices.
-   Cancelled bookings cannot be paid.
-   Completing a payment generates a transaction ID and payment date.
-   Only completed payments can be refunded.
-   Payment operations can generate notifications.

------------------------------------------------------------------------

## 9. Waitlist and Notifications

When normal booking availability is not possible, the Waitlist module
supports users waiting for availability.

``` mermaid
flowchart LR
    A[Event Unavailable]
    --> B[Join Waitlist]
    --> C[WAITING]
    --> D[Availability]
    --> E[NOTIFIED]
    --> F[Notification]
```

Duplicate waitlist entries are prevented.

Notifications support application events such as:

``` text
WAITLIST_AVAILABLE
PAYMENT_COMPLETED
PAYMENT_REFUNDED
```

Notification state follows:

``` mermaid
stateDiagram-v2
    [*] --> UNREAD
    UNREAD --> READ
```

------------------------------------------------------------------------

## 10. Review Rules

Reviews are protected by business rules rather than being available for
any event.

``` mermaid
flowchart TD
    A[Review Request]
    --> B{Event Ended?}

    B -->|No| X[Reject]
    B -->|Yes| C{Completed Booking?}

    C -->|No| X
    C -->|Yes| D{Already Reviewed?}

    D -->|Yes| X
    D -->|No| E[Create Review]
```

The system verifies:

-   The event has ended.
-   The user has a completed booking.
-   The user has not already reviewed the same event.
-   Update/delete operations respect ownership and administrative
    access.

------------------------------------------------------------------------

## 11. Security Model

The API uses Spring Security with JWT authentication.

``` mermaid
flowchart TD
    A[Request]
    --> B[JWT Security]
    --> C{Authenticated?}

    C -->|No| D[401 Unauthorized]
    C -->|Yes| E{Role Allowed?}

    E -->|No| F[403 Forbidden]
    E -->|Yes| G[Controller]
    --> H[Service]
    --> I[Ownership Validation]
```

Main roles:

``` text
USER
ORGANIZER
ADMIN
```

Authorization is based on two complementary mechanisms:

``` text
Role-Based Authorization
        +
Resource Ownership
```

Examples include:

-   Event organizer ownership
-   Booking ownership
-   Payment ownership
-   Review ownership
-   EventSeat ownership
-   Notification ownership

------------------------------------------------------------------------

## 12. Authentication Flow

Users register or login to receive a JWT.

``` mermaid
flowchart LR
    A[Register / Login]
    --> B[Authentication]
    --> C[JWT]
    --> D[Authorization Header]
    --> E[Protected Endpoint]
```

Protected requests use:

``` text
Authorization: Bearer <JWT_TOKEN>
```

The API therefore remains stateless from an authentication perspective.

------------------------------------------------------------------------

## 13. Validation and Exception Handling

Incoming DTOs are validated using Jakarta Validation.

``` mermaid
flowchart LR
    A[Request]
    --> B[DTO Validation]
    --> C{Valid?}

    C -->|No| D[400 Bad Request]
    C -->|Yes| E[Business Logic]
```

The application also uses centralized exception handling for consistent
API errors.

Important error categories include:

``` text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

------------------------------------------------------------------------

## 14. Spring Bean Management

Application components are managed by the Spring IoC Container.

``` mermaid
flowchart LR
    A[Component Discovery]
    --> B[Bean Creation]
    --> C[Dependency Injection]
    --> D[Initialization]
    --> E[Application Ready]
```

The project primarily uses constructor-based dependency injection.

Spring-managed components include:

``` text
Controllers
Services
Repositories
Mappers
Security Components
Configuration Beans
```

------------------------------------------------------------------------

## 15. Transaction Management

Business operations use Spring-managed transactions where required.

``` mermaid
flowchart TD
    A[Service Method]
    --> B[Transaction Start]
    --> C[Business Logic]
    --> D[Repository Operations]
    --> E{Successful?}

    E -->|Yes| F[Commit]
    E -->|Exception| G[Rollback]
```

Transactions help maintain consistency across operations that modify
multiple related resources.

This is especially important for workflows such as booking and
seat-status synchronization.

------------------------------------------------------------------------

## 16. Testing Strategy

The project uses automated tests across the main application layers.

``` mermaid
flowchart TD
    T[Automated Tests]

    T --> R[Repository Tests]
    T --> S[Service Tests]
    T --> C[Controller Tests]
    T --> SEC[Security Tests]

    R --> R1[Persistence & Queries]
    S --> S1[Business Rules]
    S --> S2[Ownership]
    S --> S3[State Transitions]
    C --> C1[HTTP & Validation]
    SEC --> SEC1[Authentication]
    SEC --> SEC2[Authorization]
```

The test suite covers the main modules:

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

Testing verifies more than CRUD behavior. It also covers important
business rules such as:

-   Duplicate resource prevention
-   Event ownership
-   Event status transitions
-   Venue capacity
-   Seat availability
-   Booking lifecycle
-   Payment lifecycle
-   Review eligibility
-   Waitlist behavior
-   Notification behavior
-   Authentication
-   Role authorization
-   HTTP validation

------------------------------------------------------------------------

## 17. Environment Configuration

The project separates configuration into:

``` text
dev
test
prod
```

``` mermaid
flowchart TD
    APP[Application]
    --> DEV[Development]
    --> TEST[Test]
    --> PROD[Production]
```

Development database:

``` text
event_booking_db
```

Test database:

``` text
event_booking_test_db
```

Production uses environment variables for database configuration.

Sensitive values such as:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

should be managed through environment variables rather than committed
credentials.

------------------------------------------------------------------------

## 18. API Documentation

Swagger / OpenAPI is integrated into the project.

For local development:

``` text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

``` text
http://localhost:8080/v3/api-docs
```

Swagger allows developers to inspect and test the REST endpoints
interactively.

------------------------------------------------------------------------

## 19. Key Technical Decisions

The project uses several important architectural and domain decisions.

### Layered Architecture

``` text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Keeps HTTP, business and persistence responsibilities separated.

### DTO Separation

Request and response DTOs prevent persistence entities from being
directly exposed through the API.

### Mapper Layer

Mapping logic is separated from controllers and entities.

### JWT Authentication

Provides stateless authentication for REST requests.

### Role + Ownership Authorization

Endpoint roles provide high-level authorization while service-level
ownership protects individual resources.

### EventSeat Abstraction

Separates physical venue seats from their event-specific price and
availability.

### Controlled Lifecycles

Booking, Payment, EventSeat, Waitlist and Notification domains use
explicit states and controlled transitions.

### Global Exception Handling

Provides centralized and consistent error responses.

### Layered Automated Testing

Repository, Service, Controller and Security behavior are tested
separately according to responsibility.

### Environment-Based Configuration

Development, test and production configurations are separated.

------------------------------------------------------------------------

## 20. Complete System Overview

``` mermaid
flowchart TD
    CLIENT[Client]

    CLIENT --> AUTH[JWT Authentication]
    AUTH --> CTRL[REST Controllers]

    CTRL --> SERV[Service Layer]

    SERV --> MAP[Mapper Layer]
    SERV --> REP[Repository Layer]
    REP --> DB[(MySQL)]

    SERV --> USER[User]
    SERV --> EVENT[Event]
    EVENT --> ES[EventSeat]
    ES --> BOOK[Booking]

    BOOK --> PAY[Payment]
    BOOK --> WAIT[Waitlist]
    PAY --> NOT[Notification]
    WAIT --> NOT
    BOOK --> REV[Review]
```

The system combines technical separation with domain-specific business
rules.

------------------------------------------------------------------------

## 21. End-to-End User Journey

A typical successful user journey is:

``` mermaid
flowchart TD
    A[Register / Login]
    --> B[Receive JWT]
    --> C[Browse Events]
    --> D[Select Event Seats]
    --> E[Create Booking]
    --> F[Seats RESERVED]
    --> G[Confirm Booking]
    --> H[Seats SOLD]
    --> I[Create Payment]
    --> J[Complete Payment]
    --> K[Receive Notification]
    --> L[Attend Event]
    --> M[Booking COMPLETED]
    --> N[Create Review]
```

When seats are unavailable:

``` mermaid
flowchart LR
    A[No Availability]
    --> B[Waitlist]
    --> C[WAITING]
    --> D[NOTIFIED]
    --> E[Notification]
    --> F[Booking Opportunity]
```

------------------------------------------------------------------------

## 22. Project Structure Summary

The application can be viewed as four main areas:

``` mermaid
flowchart TD
    APP[Event Booking API]

    APP --> API[API Layer]
    APP --> DOMAIN[Business Domain]
    APP --> DATA[Persistence]
    APP --> INFRA[Infrastructure]

    API --> CTRL[Controllers]
    API --> DTO[DTOs]

    DOMAIN --> SERV[Services]
    DOMAIN --> MAP[Mappers]
    DOMAIN --> RULES[Business Rules]

    DATA --> REP[Repositories]
    DATA --> ENT[Entities]
    DATA --> DB[(MySQL)]

    INFRA --> SEC[Security]
    INFRA --> JWT[JWT]
    INFRA --> EX[Exception Handling]
    INFRA --> DOC[Swagger]
    INFRA --> TEST[Testing]
```

------------------------------------------------------------------------

## 23. Final Summary

The **Event Booking API** is structured around a layered Spring Boot
architecture with clear separation between API, business logic,
persistence and security.

The core domain flow is:

``` text
User
 ↓
Authentication
 ↓
Event
 ↓
EventSeat
 ↓
Booking
 ↓
Payment
 ↓
Notification
 ↓
Review
```

When event availability is limited, the Waitlist module extends the
flow:

``` text
Event
 ↓
Waitlist
 ↓
Notification
 ↓
Booking
```

The project demonstrates:

-   REST API design
-   Spring Boot layered architecture
-   JPA entity relationships
-   DTO and Mapper separation
-   JWT authentication
-   Role-based authorization
-   Resource ownership validation
-   Business lifecycle management
-   Transaction management
-   Global exception handling
-   Swagger documentation
-   MySQL persistence
-   Automated testing across multiple layers
-   Environment-specific configuration

Together, these components form a complete backend architecture for
managing event discovery, seat availability, reservations, payments,
waitlists, notifications and reviews.
