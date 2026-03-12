## BookMyShow Evaluation Document

### Scope
This document evaluates the current `bookmyshow` implementation against the requested criteria and uses the existing Spring Boot codebase as the reference solution.

### 1. Code Artifacts

#### 1.1 API Contract
The current API surface is exposed from `BookingController` under `/api`.

| API | Method | Purpose |
|---|---|---|
| `/api/shows` | GET | Browse shows by `movieTitle`, `city`, and `date` |
| `/api/shows` | POST | Create a show |
| `/api/shows/{id}` | PUT | Update a show |
| `/api/shows/{id}` | DELETE | Delete a show |
| `/api/shows/{id}/seats` | POST | Allocate seat inventory for a show |
| `/api/bookings` | POST | Book selected seats for a show |
| `/api/bookings/bulk` | POST | Book multiple seat groups for the same show |
| `/api/bookings/{id}/cancel` | POST | Cancel a booking |
| `/api/bookings/cancel-bulk` | POST | Cancel multiple bookings |

#### 1.2 Design Patterns Used
- **Layered architecture**: `controller -> service -> model` separation is clear.
- **Single Responsibility Principle**:
  - `ShowService` manages show metadata and seat inventory.
  - `TicketService` manages booking and cancellation.
- **DTO-style request records**: `BookingRequest` and `BulkBookingRequest` simplify API input handling.
- **In-memory repository style**: `HashMap` usage acts as a lightweight store for prototype behavior.

#### 1.3 Recommended Design Patterns for Production
- **Repository pattern** for database-backed persistence.
- **Saga / workflow orchestration** for payment + booking confirmation.
- **Strategy pattern** for dynamic pricing and offer engines.
- **Adapter pattern** for external theatre and payment integrations.

#### 1.4 One Scenario Implementation
**Scenario: Seat booking for a selected show**
1. Client calls `POST /api/bookings` with `showId` and selected seats.
2. `TicketService.bookTickets()` loads the show from `ShowService`.
3. `ShowService.reserveSeats()` validates that seats exist and are not already booked.
4. Total price is calculated using `ticketPrice * numberOfSeats`.
5. A `Booking` object is created with `BOOKED` status.
6. Booking is stored and returned to the caller.

This scenario already demonstrates the core happy path for ticket booking and seat conflict prevention.

### 2. Design Principles for Functional and Non-Functional Requirements

#### 2.1 Functional Design Principles
- Keep booking flows simple and API-driven.
- Validate seat availability before confirming booking.
- Support theatre operations such as create, update, delete, and seat allocation.
- Support operational convenience with bulk booking and bulk cancellation.
- Keep pricing logic isolated so offers can evolve independently.

#### 2.2 Non-Functional Design Principles
- **Scalability**: stateless services with externalized data storage.
- **Consistency**: transactional seat reservation and booking creation.
- **Performance**: cache show listings and frequently accessed seat state.
- **Reliability**: prevent double booking using locking/atomic reservation.
- **Maintainability**: clear service boundaries and small focused classes.
- **Security**: input validation, API auth, audit logging, secure payment integration.
- **Observability**: metrics, logs, and trace IDs for booking flows.

### 3. DB & Data Model

#### 3.1 Current State
The current implementation uses in-memory `HashMap` storage in `ShowService` and `TicketService`. This is suitable for a prototype or coding exercise but not for distributed production use.

#### 3.2 Proposed Persistent Data Model
Core entities:
- **Show**: `id`, `theatre_name`, `city`, `movie_title`, `show_date`, `show_time`, `ticket_price`
- **SeatInventory**: `show_id`, `seat_id`, `status`
- **Booking**: `id`, `show_id`, `total_price`, `status`, `created_at`
- **BookingSeat**: `booking_id`, `seat_id`

#### 3.3 Relationship View
- One **Show** has many seats.
- One **Booking** belongs to one show.
- One **Booking** contains many booked seats.
- `SeatInventory.status` can be `AVAILABLE`, `HELD`, or `BOOKED` in an extensible model.

#### 3.4 DB Choice
- **PostgreSQL** for transactional consistency.
- **Redis** for temporary seat holds and high-speed contention control.
- Optional **Kafka** for booking, payment, and notification events.

### 4. Platform Solutions Detailing
- **Backend stack**: Java 17 + Spring Boot REST APIs.
- **Packaging**: Maven-based build.
- **Deployment target**: containerized service on Kubernetes or a managed cloud platform.
- **Runtime dependencies**:
  - PostgreSQL for system of record.
  - Redis for caching and seat hold workflows.
  - API gateway for routing, auth, and throttling.
- **CI/CD**:
  - Build + unit tests on every commit.
  - Canary or blue-green deployment for safer releases.
- **Monitoring**:
  - Application metrics for booking success rate and latency.
  - Logs with booking ID / show ID correlation.
  - Alerts for seat contention spikes and failed bookings.

### 5. Solution Completeness, Presentation, and Discussion

#### 5.1 What Is Already Complete
- Core REST APIs for show and booking management.
- Seat inventory allocation.
- Seat reservation conflict validation.
- Booking and cancellation flow.
- Bulk booking and bulk cancellation support.

#### 5.2 What Should Be Added for a Complete Production Solution
- Persistent database integration.
- Authentication and authorization.
- Payment workflow.
- Seat hold timeout support.
- Validation/error handling standards.
- Unit and integration tests.
- Audit trail and notifications.

### 6. Solution Coverage, Uniqueness, and Extensibility
- Covers both **customer booking flows** and **theatre management flows**.
- Includes **bulk operations**, which is a useful differentiator for group/corporate bookings.
- Pricing logic can be extended to support offers, coupons, and surge pricing.
- Architecture can evolve from monolith to modular services without redesigning the API contract.
- Data model supports future features such as payment state, seat hold expiry, loyalty, and multi-city rollout.

### Conclusion
The current solution is a strong foundation for a movie ticket booking platform. It already demonstrates API design, service separation, and seat conflict handling. To score well on evaluation criteria, the best presentation is to position the existing code as a working prototype and pair it with a production-ready roadmap covering persistence, reliability, scalability, and extensibility.

