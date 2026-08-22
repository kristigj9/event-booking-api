# Event Booking API — Architecture

## 1. Architecture Overview

The application follows a layered architecture where each layer has a specific responsibility.

```mermaid
flowchart TD
    A[Client / Postman / Swagger]
    --> B[Spring Security]
    --> C[Controller Layer]
    --> D[Service Layer]
    --> E[Repository Layer]
    --> F[Entity Layer]
    --> G[(MySQL Database)]
```

This separation keeps the API, business logic, persistence logic, and database responsibilities independent.

---

## 2. Main Layers

| Layer | Responsibility |
|---|---|
| **Controller** | Handles HTTP requests and responses |
| **DTO** | Defines request and response models |
| **Service** | Contains business logic |
| **Mapper** | Converts between DTOs and entities |
| **Repository** | Handles database access |
| **Entity** | Represents database tables |
| **Security** | Authentication and authorization |

---

## 3. Request Flow

A typical request moves through the application as follows:

```mermaid
flowchart LR
    A[HTTP Request]
    --> B[Security Filter]
    --> C[Controller]
    --> D[Service]
    --> E[Repository]
    --> F[(Database)]
```

The response returns in the opposite direction:

```mermaid
flowchart LR
    A[(Database)]
    --> B[Repository]
    --> C[Service]
    --> D[Mapper]
    --> E[Response DTO]
    --> F[Controller]
    --> G[HTTP Response]
```

---

## 4. DTO and Mapper Flow

DTOs are used to avoid exposing JPA entities directly through the API.

```mermaid
flowchart LR
    A[Request DTO]
    --> B[Controller]
    --> C[Service]
    --> D[Mapper]
    --> E[Entity]

    E --> F[Repository]
```

For responses:

```mermaid
flowchart LR
    A[Entity]
    --> B[Mapper]
    --> C[Response DTO]
    --> D[Controller]
    --> E[Client]
```

---

## 5. Service Structure

The Service Layer separates interfaces from implementations.

```mermaid
flowchart LR
    A[Controller]
    --> B[Service Interface]
    --> C[Service Implementation]
    --> D[Repository]
```

Example:

```text
BookingController
        ↓
BookingService
        ↓
BookingServiceImpl
        ↓
BookingRepository
```

This improves separation of concerns and makes the application easier to test.

---

## 6. Security Flow

Protected requests pass through JWT authentication before reaching the controller.

```mermaid
flowchart LR
    A[Client]
    --> B[Bearer JWT]
    --> C[JWT Filter]
    --> D[Authentication]
    --> E[Authorization]
    --> F[Controller]
```

Authorization checks can include:

- Authentication
- User role
- Resource ownership

---

## 7. Role and Ownership Check

```mermaid
flowchart TD
    A[Authenticated Request]
    --> B{Correct Role?}

    B -->|No| C[Access Denied]
    B -->|Yes| D{Ownership Required?}

    D -->|No| E[Operation Allowed]
    D -->|Yes| F{Owns Resource?}

    F -->|Yes| E
    F -->|No| C
```

This is especially important for `ORGANIZER` operations.

An organizer may have permission to manage events, but should only modify events they own.

---

## 8. Booking Request Example

A booking operation demonstrates how the main layers work together.

```mermaid
sequenceDiagram
    participant U as User
    participant C as BookingController
    participant S as BookingService
    participant R as BookingRepository
    participant DB as MySQL

    U->>C: Create Booking Request
    C->>S: createBooking()
    S->>R: save()
    R->>DB: INSERT
    DB-->>R: Booking
    R-->>S: Booking
    S-->>C: BookingResponse
    C-->>U: HTTP Response
```

---

## 9. Complete Architecture

```mermaid
flowchart TD
    CLIENT[Client / Swagger / Postman]

    CLIENT --> SECURITY[Spring Security + JWT]
    SECURITY --> CONTROLLER[Controllers]

    CONTROLLER --> DTO[Request / Response DTOs]
    CONTROLLER --> SERVICE[Services]

    SERVICE --> MAPPER[Mappers]
    SERVICE --> REPOSITORY[Repositories]

    REPOSITORY --> ENTITY[Entities]
    ENTITY --> DB[(MySQL Database)]
```

---

## 10. Architecture Summary

The main dependency flow is:

```text
Client
  ↓
Security
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
```

Supporting components:

```text
DTO      → API communication
Mapper   → DTO / Entity conversion
Security → Authentication and authorization
Exception Handler → Centralized error responses
```

This architecture keeps the application modular, testable, and easier to maintain.

---

**Next:** `03-DATABASE-DESIGN.md`