## BookMyShow – System Overview

This document summarizes the design and implementation across **four key areas**:

- **1. Functional features**
- **2. Non‑functional architecture & quality attributes**
- **3. Platform provisioning, sizing & release**
- **4. Product & stakeholder management**

---

## 1. Functional Features

### 1.1 Core booking flows

- **Browse shows**
  - Search theatres currently running a selected movie in a city on a given date.
  - View show timings, theatre, city, and base ticket price.

- **Seat selection & booking**
  - Select a theatre, show timing, and preferred seats for the day.
  - Validate seat availability to prevent double booking.

- **Bulk booking & cancellation**
  - Book multiple groups of seats for the same show in a single request.
  - Bulk-cancel a set of bookings (e.g. group bookings, corporate bookings).

### 1.2 Theatre operations

- **Show lifecycle management**
  - Create, update, and delete shows for a specific day and screen.
  - Manage show metadata (movie, timing, pricing, theatre, city).

- **Seat inventory management**
  - Allocate seat inventory for each show (e.g. A1–A10, B1–B10).
  - Update inventory when layout or capacity changes.
  - Track booked vs available seats per show.

### 1.3 Offers & pricing rules

- **City/theatre specific offers**
  - Booking platform offers only in selected cities and theatres.

- **Ticket-level discounts**
  - 50% discount on the **third ticket** in a booking.
  - 20% discount for **afternoon shows** (configurable time window).

---

## 2. Non‑Functional Requirements & Architecture

### 2.1 High‑level architecture

- **Service decomposition**
  - `ShowService`: manage theatres, shows, and seat inventory.
  - `TicketService` / `BookingService`: manage bookings, bulk booking & cancellation.
  - `PaymentService`: abstraction over payment gateways (Stripe, Razorpay, Adyen, etc.).
  - `TheatreIntegrationService`: adapters for legacy and new theatre IT systems.
  - `SearchService`: optimized read model for show discovery.

- **Key technologies**
  - Backend: **Java, Spring Boot** (REST APIs, controllers, services).
  - Data: **PostgreSQL** (transactions), **Redis** (caching, seat state), optional **Kafka** (events).
  - API: **REST + JSON**, secured via OAuth2 / OpenID Connect.

### 2.2 Transactions & consistency

- **Seat selection & booking**
  - Seats are **held** for a short time (e.g. 5 minutes) using atomic operations or Redis scripts.
  - Payment is processed while seats are held.
  - Booking is confirmed only after successful payment; otherwise, seats are released.
  - Implemented via **Saga pattern** and **event-driven** workflows (e.g. `PAYMENT_AUTHORIZED`, `BOOKING_CONFIRMED`).

- **Bulk operations**
  - Bulk booking and cancellation handled as batches of idempotent commands.
  - Option to choose “best‑effort” (partial success allowed) or “all‑or‑nothing” (compensating cancellations when partial failure occurs).

### 2.3 Integration & localization

- **Theatre integrations**
  - Existing IT systems: integrate via REST/SOAP/file feeds/message queues through canonical models.
  - New theatres: self‑service APIs/portal to push shows, seat maps, and pricing.

- **Localization**
  - Movie metadata is stored with language and region (e.g. `hi-IN`, `en-US`).
  - Time and currency are handled per theatre region, displayed in the user’s locale.

### 2.4 Scalability & availability

- **Scalability**
  - Stateless services running on container orchestration (Kubernetes) with horizontal auto‑scaling.
  - Caching with Redis for show listings and seat availability to reduce DB load.
  - Partitioned and indexed database tables (e.g. bookings by region and date).

- **Availability (target 99.99%)**
  - Multi‑AZ deployment; multi‑region for key markets.
  - Circuit breakers, retries with backoff, and graceful degradation (per‑theatre isolation).
  - Health checks, blue‑green/canary deployments, and automated rollback.

### 2.5 Security, compliance & OWASP

- **Security controls**
  - Strong authentication and authorization (JWT/OAuth2, role‑based access).
  - Input validation and parameterized queries to prevent injection.
  - TLS everywhere; encryption at rest for sensitive data.
  - Centralized secrets management (e.g. KMS, Vault).

- **OWASP Top 10 coverage (high level)**
  - Access control enforced at API and service layers.
  - No raw card data stored; use tokenization and hosted payment pages.
  - Secure configuration via IaC and hardened baselines.
  - Logging, monitoring, and anomaly detection for auth, booking, and payment flows.

- **Compliance**
  - **PCI‑DSS**: scope minimized by delegating card handling to gateway providers.
  - **GDPR/CCPA**: data minimization, explicit consent, right‑to‑access/delete, and regional data residency where required.

---

## 3. Platform, Provisioning & Release

### 3.1 Hosting & sizing

- **Cloud model**
  - Primary assumption: **public cloud** (e.g. AWS). Hybrid/multi‑cloud possible with similar concepts.
  - Kubernetes (EKS/GKE/AKS) for microservices, auto‑scaling across multiple AZs.

- **Initial sizing (example)**
  - 3 worker nodes per region (e.g. 2–4 vCPU, 8–16 GB RAM) for application services.
  - Managed Postgres (multi‑AZ) and Redis cluster for caching.
  - CDN for static content, edge caching for read‑heavy APIs (e.g. show listings).

### 3.2 Release management & internationalization

- **Releases**
  - Trunk‑based development with feature flags for risky changes.
  - Blue‑green or canary deployments per region; start with low traffic slice, then roll out.
  - Database migrations via Flyway/Liquibase with expand–migrate–contract strategy.

- **Across geographies**
  - Region‑specific deployments for latency and compliance.
  - Staggered releases across regions to limit blast radius and enable quick rollback.

- **Internationalization**
  - Backend and frontend support multiple locales and currencies.
  - Configurable tax rules, payment methods, and legal text per country/region.

### 3.3 Monitoring & logging

- **Monitoring**
  - Metrics via Prometheus/OpenTelemetry to Datadog/New Relic/etc.
  - Dashboards for booking throughput, error rates, latency, payment success, integration SLAs.

- **Logging & tracing**
  - Structured JSON logs with correlation IDs, shipped to ELK/OpenSearch.
  - Distributed tracing (OpenTelemetry) from gateway through booking, payment, and theatre integrations.
  - Alerts for SLO violations, spikes in 4xx/5xx, payment failures, and seat contention.

### 3.4 KPIs

- **Business KPIs**
  - Conversion rate (search → booking).
  - Revenue, commissions, average order value.
  - Repeat bookings, customer satisfaction (NPS).

- **Operational KPIs**
  - Service availability and p95/p99 latency.
  - Booking and payment success rates.
  - Mean time to detect (MTTD) and mean time to recover (MTTR).
  - Theatre integration uptime and error rates.

---

## 4. Product & Stakeholder Management

### 4.1 Stakeholder management

- Balance needs of:
  - **Product & marketing** (features, offers, UX),
  - **Theatres** (control over shows, pricing, inventory),
  - **Finance & compliance** (payments, invoicing, audits),
  - **Engineering & operations** (reliability, scalability).
- Use **short workshops and ADRs (Architecture Decision Records)** to document decisions, trade‑offs, and owners.
- Time‑box investigations/POCs to avoid analysis paralysis and drive decision closure.

### 4.2 Technology & team enablement

- Define a **target architecture** and “golden paths” for service creation, logging, security, and testing.
- Improve developer experience with:
  - Local dev environments (dockerized dependencies),
  - Standard templates for new services,
  - Automated CI/CD with quality gates (tests, linting, coverage).
- Reserve capacity for **technical debt reduction** and **non‑functional improvements** (performance, security, observability).

### 4.3 Delivery planning & estimates

- Plan via:
  - Epics mapped to major capabilities (Search, Booking, Payments, Integrations, Observability, Security).
  - Story‑level estimates (points or T‑shirt sizes) and velocity‑based forecasting.
- Maintain a visible roadmap and single delivery board, with:
  - Weekly status (what shipped, what slipped, why),
  - Explicitly tracked risks and external dependencies (payment certifications, partner readiness),
  - Scope/dates adjusted based on real progress, not optimistic assumptions.

