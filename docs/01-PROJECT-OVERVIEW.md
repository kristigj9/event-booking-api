# Event Booking API — Project Overview

## 1. Project Purpose

**Event Booking API** is a RESTful backend application for managing events and the complete booking process.

The system supports three main roles:

- **USER** — browses events, books seats, makes payments, joins waitlists and submits reviews.
- **ORGANIZER** — creates and manages owned events, event seats, bookings and waitlists.
- **ADMIN** — manages users, roles, categories, venues and system-level resources.

---

## 2. Main Modules

| Module | Purpose |
|---|---|
| **Auth** | Registration and authentication |
| **User** | User profiles and roles |
| **Category** | Event categories |
| **Venue** | Event locations |
| **Seat** | Physical seats inside venues |
| **Event** | Event management |
| **EventSeat** | Seat configuration for a specific event |
| **Booking** | Event reservations |
| **BookingSeat** | Seats assigned to a booking |
| **Payment** | Booking payments |
| **Waitlist** | Waiting list management |
| **Review** | Event reviews |
| **Notification** | User notifications |

---

# 3. Complete Event Booking Flow

The main user journey starts with event discovery and continues through seat selection, booking and payment.

```mermaid
flowchart LR
    A[User] --> B[Browse Events]
    B --> C[Select Event]
    C --> D[Select Seats]
    D --> E{Seats Available?}

    E -->|Yes| F[Create Booking]
    F --> G[Payment]
    G --> H[Booking Confirmed]
    H --> I[Notification]

    E -->|No| J[Join Waitlist]
```

If the selected seats are available, the user continues with the booking process.

If no seats are available, the user can join the waitlist.

---

# 4. Organizer Flow

An organizer manages the complete lifecycle of owned events.

```mermaid
flowchart LR
    A[Organizer] --> B[Create Event]
    B --> C[Assign Venue]
    C --> D[Configure Event Seats]
    D --> E[Manage Event]
    E --> F[Manage Bookings]
    F --> G[Manage Waitlist]
```

Ownership rules ensure that an organizer manages only the resources related to their own events.

---

# 5. Seat and Booking Structure

The system separates physical seats from seats configured for a specific event.

```mermaid
flowchart TD
    V[Venue] --> S[Seat]

    V --> E[Event]

    S --> ES[EventSeat]
    E --> ES

    ES --> BS[BookingSeat]
    B[Booking] --> BS

    U[User] --> B
    E --> B

    B --> P[Payment]
```

### Main Relationships

- A **Venue** contains physical `Seat` records.
- An **Event** takes place in a venue.
- An **EventSeat** connects a physical seat with a specific event.
- A **BookingSeat** connects an event seat with a booking.
- A **Booking** belongs to a user and an event.
- A **Payment** is associated with a booking.

This structure allows the same physical seat to be reused across different events.

---

# 6. Waitlist Flow

When booking is not immediately possible, the user can join the event waitlist.

```mermaid
flowchart TD
    A[User Selects Event] --> B{Seats Available?}

    B -->|Yes| C[Create Booking]

    B -->|No| D[Join Waitlist]
    D --> E[WAITING]

    E --> F{Seat Becomes Available?}

    F -->|No| E
    F -->|Yes| G[Notify User]

    G --> H[Convert Waitlist]
    H --> C
```

The waitlist provides an alternative path to booking when availability changes later.

---

# 7. Roles and Responsibilities

The system separates responsibilities between `USER`, `ORGANIZER` and `ADMIN`.

```mermaid
flowchart TD
    API[Event Booking API]

    API --> USER[USER]
    API --> ORG[ORGANIZER]
    API --> ADMIN[ADMIN]

    USER --> U1[Browse Events]
    USER --> U2[Create Bookings]
    USER --> U3[Payments]
    USER --> U4[Waitlists]
    USER --> U5[Reviews]

    ORG --> O1[Own Events]
    ORG --> O2[Event Seats]
    ORG --> O3[Event Bookings]
    ORG --> O4[Event Waitlists]

    ADMIN --> A1[Users & Roles]
    ADMIN --> A2[Categories]
    ADMIN --> A3[Venues]
    ADMIN --> A4[System Management]
```

---

# 8. Project Summary

The main business flow of the application is:

```mermaid
flowchart LR
    U[User]
    --> E[Event]
    --> ES[EventSeat]
    --> B[Booking]
    --> P[Payment]
    --> N[Notification]
```

When immediate booking is not possible:

```mermaid
flowchart LR
    U[User]
    --> E[Event]
    --> W[Waitlist]
    --> N[Notification]
    --> B[Booking]
```

**Event Booking API** combines event management, venue and seat configuration, reservations, payments, waitlists, reviews and notifications in a single backend system.

---

**Next:** `02-ARCHITECTURE.md`