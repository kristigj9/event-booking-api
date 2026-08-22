# Event Booking API --- API Usage

## 1. Overview

This guide provides a practical overview of how to use the **Event
Booking API**.

The API supports the main application domains:

-   Authentication
-   Users
-   Categories
-   Venues
-   Seats
-   Events
-   Event Seats
-   Bookings
-   Payments
-   Reviews
-   Waitlists
-   Notifications

Most protected operations require a valid JWT token.

``` mermaid
flowchart LR
    A[Client]
    --> B[REST API]
    --> C[Spring Security]
    --> D[Controller]
    --> E[Service]
    --> F[Repository]
    --> G[(MySQL)]
```

------------------------------------------------------------------------

## 2. Base URL

For local development, the API runs on:

``` text
http://localhost:8080
```

All main REST endpoints use the `/api` prefix.

Example:

``` text
http://localhost:8080/api/events
```

------------------------------------------------------------------------

## 3. Authentication

Authentication is based on JWT.

### Register

``` http
POST /api/auth/register
```

The request uses the project's `RegisterRequest` DTO.

Successful registration returns:

``` text
201 Created
```

and an authentication response containing a JWT token.

### Login

``` http
POST /api/auth/login
```

The request uses the project's `LoginRequest` DTO.

Successful login returns:

``` text
200 OK
```

and a JWT token.

``` mermaid
flowchart LR
    A[Register / Login]
    --> B[Authentication]
    --> C[JWT Token]
    --> D[Protected API]
```

------------------------------------------------------------------------

## 4. Using the JWT Token

Protected endpoints require the token in the HTTP `Authorization`
header:

``` text
Authorization: Bearer <JWT_TOKEN>
```

The security layer validates the token before allowing the request to
reach protected application operations.

``` mermaid
flowchart TD
    A[HTTP Request]
    --> B{JWT Present and Valid?}

    B -->|No| C[401 Unauthorized]
    B -->|Yes| D[Authenticated User]
    --> E{Role Allowed?}

    E -->|No| F[403 Forbidden]
    E -->|Yes| G[Controller]
```

------------------------------------------------------------------------

## 5. Roles and Access

The application uses role-based access control.

Main roles:

``` text
USER
ORGANIZER
ADMIN
```

Typical responsibilities are:

  -----------------------------------------------------------------------
  Role                                Main Access
  ----------------------------------- -----------------------------------
  `USER`                              Browse events, create/manage own
                                      bookings, payments, reviews,
                                      waitlist and notifications

  `ORGANIZER`                         User operations plus event
                                      management and organizer-specific
                                      operations

  `ADMIN`                             Administrative access and protected
                                      management operations
  -----------------------------------------------------------------------

Some authorization rules also depend on **ownership**, not only role.

Examples:

``` text
Event Organizer Ownership
Booking Ownership
Payment Ownership
Review Ownership
Notification Ownership
EventSeat Ownership
```

------------------------------------------------------------------------

# 6. User API

Base path:

``` text
/api/users
```

  Method   Endpoint                   Purpose
  -------- -------------------------- ------------------------------
  GET      `/api/users/me`            Get current user profile
  PUT      `/api/users/me`            Update current user profile
  PATCH    `/api/users/me/password`   Change current user password
  GET      `/api/users`               Get all users
  GET      `/api/users/{id}`          Get user by ID
  PATCH    `/api/users/{id}/role`     Update user role
  DELETE   `/api/users/{id}`          Delete user

Administrative user operations are restricted to `ADMIN`.

------------------------------------------------------------------------

# 7. Category API

Base path:

``` text
/api/categories
```

  Method   Endpoint                 Access
  -------- ------------------------ --------
  GET      `/api/categories`        Public
  GET      `/api/categories/{id}`   Public
  POST     `/api/categories`        ADMIN
  PUT      `/api/categories/{id}`   ADMIN
  DELETE   `/api/categories/{id}`   ADMIN

Categories are used to classify events.

``` mermaid
flowchart LR
    A[Category]
    --> B[Event]
```

------------------------------------------------------------------------

# 8. Venue API

Base path:

``` text
/api/venues
```

  Method   Endpoint             Access
  -------- -------------------- --------
  GET      `/api/venues`        Public
  GET      `/api/venues/{id}`   Public
  POST     `/api/venues`        ADMIN
  PUT      `/api/venues/{id}`   ADMIN
  DELETE   `/api/venues/{id}`   ADMIN

A venue contains the physical seats used by events.

``` mermaid
flowchart LR
    A[Venue]
    --> B[Seat]
    A --> C[Event]
```

------------------------------------------------------------------------

# 9. Seat API

Base path:

``` text
/api/seats
```

  Method   Endpoint                       Access
  -------- ------------------------------ --------
  GET      `/api/seats`                   Public
  GET      `/api/seats/{id}`              Public
  GET      `/api/seats/venue/{venueId}`   Public
  POST     `/api/seats`                   ADMIN
  PUT      `/api/seats/{id}`              ADMIN
  DELETE   `/api/seats/{id}`              ADMIN

A `Seat` represents a physical seat belonging to a venue.

``` mermaid
flowchart LR
    A[Venue]
    --> B[Physical Seat]
    --> C[EventSeat]
```

`EventSeat` represents the use of that physical seat for a specific
event.

------------------------------------------------------------------------

# 10. Event API

Base path:

``` text
/api/events
```

  ---------------------------------------------------------------------------------------
  Method                  Endpoint                                Purpose
  ----------------------- --------------------------------------- -----------------------
  GET                     `/api/events`                           Get all events

  GET                     `/api/events/{id}`                      Get event by ID

  GET                     `/api/events/organizer/{organizerId}`   Get events by organizer

  GET                     `/api/events/status/{status}`           Get events by status

  GET                     `/api/events/category/{categoryId}`     Get events by category

  GET                     `/api/events/venue/{venueId}`           Get events by venue

  POST                    `/api/events`                           Create event

  PUT                     `/api/events/{eventId}`                 Update event

  DELETE                  `/api/events/{eventId}`                 Delete event
  ---------------------------------------------------------------------------------------

Public users can browse events.

Creation is available to:

``` text
ORGANIZER / ADMIN
```

Update and delete operations are protected by organizer ownership or
administrative access.

``` mermaid
flowchart LR
    A[ORGANIZER / ADMIN]
    --> B[Create Event]
    --> C[Event]
    --> D[Assign Event Seats]
```

------------------------------------------------------------------------

# 11. Event Seat API

Base path:

``` text
/api/event-seats
```

  ----------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                   Purpose
  ----------------------- ---------------------------------------------------------- -----------------------
  POST                    `/api/event-seats/event/{eventId}`                         Assign seat to event

  GET                     `/api/event-seats/{id}`                                    Get EventSeat by ID

  GET                     `/api/event-seats/event/{eventId}`                         Get seats assigned to
                                                                                     event

  GET                     `/api/event-seats/event/{eventId}/status/{status}`         Filter event seats by
                                                                                     status

  PATCH                   `/api/event-seats/{eventSeatId}/price?priceSeat={price}`   Update seat price

  DELETE                  `/api/event-seats/{eventSeatId}`                           Remove EventSeat
  ----------------------------------------------------------------------------------------------------------

Management operations are intended for the event organizer or `ADMIN`.

Main EventSeat states include:

``` text
AVAILABLE
RESERVED
SOLD
```

``` mermaid
stateDiagram-v2
    AVAILABLE --> RESERVED: Booking created
    RESERVED --> SOLD: Booking confirmed
    RESERVED --> AVAILABLE: Booking cancelled
    SOLD --> AVAILABLE: Booking cancelled when allowed
```

------------------------------------------------------------------------

# 12. Booking API

The Booking module manages reservations between users, events and event
seats.

The main operations include:

``` text
Create Booking
Get Booking
Get My Bookings
Get Bookings by Event
Get My Bookings by Status
Confirm Booking
Cancel Booking
Complete Booking
```

The exact endpoint mappings should follow the project's
`BookingController`.

### Booking Lifecycle

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> CONFIRMED
    PENDING --> CANCELLED
    CONFIRMED --> CANCELLED
    CONFIRMED --> COMPLETED
```

### Seat Synchronization

When a booking is created:

``` text
EventSeat: AVAILABLE → RESERVED
```

When a booking is confirmed:

``` text
EventSeat: RESERVED → SOLD
```

When an eligible booking is cancelled:

``` text
EventSeat: RESERVED / SOLD → AVAILABLE
```

``` mermaid
flowchart LR
    A[Select Event]
    --> B[Select Available Seats]
    --> C[Create Booking]
    --> D[PENDING]
    --> E[Seats RESERVED]
    --> F[Confirm Booking]
    --> G[CONFIRMED]
    --> H[Seats SOLD]
```

------------------------------------------------------------------------

# 13. Payment API

Base path:

``` text
/api/payments
```

  Method   Endpoint                               Purpose
  -------- -------------------------------------- ------------------------
  POST     `/api/payments/booking/{bookingId}`    Create payment
  GET      `/api/payments/{paymentId}`            Get payment by ID
  GET      `/api/payments/booking/{bookingId}`    Get payment by booking
  PATCH    `/api/payments/{paymentId}/complete`   Complete payment
  PATCH    `/api/payments/{paymentId}/refund`     Refund payment

Payment access is protected by booking/payment ownership or
administrative access.

### Payment Lifecycle

``` mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> COMPLETED
    COMPLETED --> REFUNDED
```

Payment amount is calculated from the selected event-seat prices
according to the service business logic.

Completing a payment generates a transaction identifier and payment
date.

------------------------------------------------------------------------

# 14. Review API

Base path:

``` text
/api/reviews
```

  Method   Endpoint                         Purpose
  -------- -------------------------------- ----------------------------
  POST     `/api/reviews`                   Create review
  GET      `/api/reviews/{reviewId}`        Get review
  GET      `/api/reviews/event/{eventId}`   Get reviews by event
  GET      `/api/reviews/me`                Get current user's reviews
  PUT      `/api/reviews/{reviewId}`        Update review
  DELETE   `/api/reviews/{reviewId}`        Delete review

Public users can read event reviews.

Creating a review requires business-rule validation.

``` mermaid
flowchart TD
    A[Create Review]
    --> B{Event Ended?}

    B -->|No| X[Reject]
    B -->|Yes| C{Completed Booking?}

    C -->|No| X
    C -->|Yes| D{Already Reviewed?}

    D -->|Yes| X
    D -->|No| E[Create Review]
```

A review can only be updated by its owner or according to administrative
authorization.

------------------------------------------------------------------------

# 15. Waitlist API

The Waitlist module is used when a user needs to wait for event
availability.

Main operations include:

``` text
Join Event Waitlist
Get My Waitlist Entries
Get Event Waitlist
Filter Event Waitlist by Status
Mark Entry as Notified
Convert Waitlist Entry
Cancel Waitlist Entry
```

The exact endpoint mappings should follow the project's
`WaitlistController`.

### Waitlist Flow

``` mermaid
flowchart LR
    A[Event Unavailable]
    --> B[Join Waitlist]
    --> C[WAITING]
    --> D[Availability Detected]
    --> E[NOTIFIED]
    --> F[Notification]
```

Duplicate waitlist entries are prevented by the service business rules.

------------------------------------------------------------------------

# 16. Notification API

Base path:

``` text
/api/notifications
```

  --------------------------------------------------------------------------------------------
  Method                  Endpoint                                     Purpose
  ----------------------- -------------------------------------------- -----------------------
  GET                     `/api/notifications/me`                      Get my notifications

  GET                     `/api/notifications/me/unread`               Get unread
                                                                       notifications

  GET                     `/api/notifications/me/unread/count`         Count unread
                                                                       notifications

  GET                     `/api/notifications/{notificationId}`        Get notification by ID

  PATCH                   `/api/notifications/{notificationId}/read`   Mark notification as
                                                                       read

  PATCH                   `/api/notifications/me/read-all`             Mark all notifications
                                                                       as read
  --------------------------------------------------------------------------------------------

### Notification State

``` mermaid
stateDiagram-v2
    [*] --> UNREAD
    UNREAD --> READ
```

Notifications are generated by important application workflows such as
payment and waitlist operations.

------------------------------------------------------------------------

# 17. Main Booking and Payment Flow

A typical successful API flow is:

``` mermaid
flowchart TD
    A[Register / Login]
    --> B[Receive JWT]
    --> C[Browse Events]
    --> D[Get Event Seats]
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

This flow demonstrates the collaboration between:

``` text
Authentication
Event
EventSeat
Booking
Payment
Notification
```

------------------------------------------------------------------------

# 18. Cancellation Flow

Booking cancellation also affects seat availability.

``` mermaid
flowchart LR
    A[Booking]
    --> B[Cancel]
    --> C[CANCELLED]
    --> D[Release Seats]
    --> E[EventSeat AVAILABLE]
    --> F[Restore Available Seat Count]
```

This keeps booking state and seat availability synchronized.

------------------------------------------------------------------------

# 19. Review Flow After Event Completion

After an event has ended and the user has a completed booking:

``` mermaid
flowchart LR
    A[Event Ended]
    --> B[Completed Booking]
    --> C[Create Review]
    --> D[Review Stored]
    --> E[Public Event Reviews]
```

The API prevents duplicate reviews for the same user and event.

------------------------------------------------------------------------

# 20. HTTP Status Codes

The API uses standard HTTP response codes.

  Status               Meaning
  -------------------- --------------------------------------------
  `200 OK`             Request completed successfully
  `201 Created`        Resource created successfully
  `204 No Content`     Operation successful with no response body
  `400 Bad Request`    Invalid request or business-rule violation
  `401 Unauthorized`   Authentication required or invalid
  `403 Forbidden`      Authenticated but not authorized
  `404 Not Found`      Requested resource does not exist
  `409 Conflict`       Duplicate or conflicting resource

------------------------------------------------------------------------

# 21. Validation and Error Handling

Request DTOs use Jakarta Bean Validation.

Controllers use:

``` java
@Valid
@RequestBody
```

Invalid request data is rejected before the service operation proceeds.

``` mermaid
flowchart TD
    A[HTTP Request]
    --> B[DTO Validation]
    --> C{Valid?}

    C -->|No| D[400 Bad Request]
    C -->|Yes| E[Controller]
    --> F[Service]
```

Business exceptions are handled through the application's global
exception handling mechanism.

Typical error categories include:

``` text
Validation Error
Resource Not Found
Conflict
Access Denied
Authentication Error
Business Rule Violation
```

------------------------------------------------------------------------

# 22. Swagger Usage

Start the application and open:

``` text
http://localhost:8080/swagger-ui/index.html
```

Recommended testing sequence:

``` mermaid
flowchart TD
    A[Open Swagger]
    --> B[Register / Login]
    --> C[Copy JWT]
    --> D[Authorize]
    --> E[Test Public Endpoints]
    --> F[Test Protected Endpoints]
```

For protected requests, use:

``` text
Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# 23. API Access Summary

  -----------------------------------------------------------------------------
  Module             Public Read   Authenticated       Organizer          Admin
                                            User                 
  --------------- -------------- --------------- --------------- --------------
  Auth                       Yes             Yes             Yes            Yes

  Users                       No     Own profile     Own profile     Management

  Categories                 Yes            Read            Read     Management

  Venues                     Yes            Read            Read     Management

  Seats                      Yes            Read            Read     Management

  Events                     Yes            Read   Create/manage     Management
                                                  allowed events 

  Event Seats                Yes            Read    Manage owned     Management
                                                     event seats 

  Bookings                    No    Own bookings   Event-related     Management
                                                      operations 

  Payments                    No    Own payments    According to     Management
                                                 ownership rules 

  Reviews            Public read      Own review      Own review     Management
                                      operations      operations 

  Waitlist                    No     Own entries  Event waitlist     Management
                                                      operations 

  Notifications               No             Own             Own     Authorized
                                   notifications   notifications         access
  -----------------------------------------------------------------------------

> Ownership and business-rule checks are enforced in the service layer
> in addition to endpoint-level role authorization.

------------------------------------------------------------------------

# 24. Complete API Usage Flow

``` mermaid
flowchart TD
    AUTH[Register / Login]
    --> JWT[JWT]

    JWT --> EVENTS[Browse Events]
    EVENTS --> ES[View Event Seats]

    ES --> AVAILABLE{Seats Available?}

    AVAILABLE -->|Yes| BOOK[Create Booking]
    AVAILABLE -->|No| WAIT[Join Waitlist]

    WAIT --> WN[Waitlist Notification]
    WN --> BOOK

    BOOK --> CONFIRM[Confirm Booking]
    CONFIRM --> PAYMENT[Create Payment]
    PAYMENT --> COMPLETE[Complete Payment]
    COMPLETE --> PN[Payment Notification]

    CONFIRM --> EVENTEND[Event Ends]
    EVENTEND --> BC[Complete Booking]
    BC --> REVIEW[Create Review]
```

------------------------------------------------------------------------

# 25. Summary

The API follows a secured REST workflow built around JWT authentication
and role/ownership authorization.

The main user journey is:

``` text
Authenticate
    ↓
Browse Events
    ↓
Select Event Seats
    ↓
Create Booking
    ↓
Confirm Booking
    ↓
Create Payment
    ↓
Complete Payment
    ↓
Receive Notifications
    ↓
Complete Event
    ↓
Create Review
```

When seats are unavailable:

``` text
Event
  ↓
Waitlist
  ↓
Notification
  ↓
Booking
```

Together, the API modules provide the complete event-booking lifecycle
while keeping authentication, authorization, seat availability, booking
state, payments, reviews, waitlists and notifications separated into
dedicated application domains.

------------------------------------------------------------------------

**Next:** `11-PROJECT-SUMMARY.md`
