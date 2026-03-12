# BookMyShow Platform - Architecture Design Document

## Table of Contents
1. [Functional Requirements Implementation](#1-functional-requirements-implementation)
2. [Transactional Scenarios & Design Decisions](#2-transactional-scenarios--design-decisions)
3. [Theatre Integration Architecture](#3-theatre-integration-architecture)
4. [Scaling & Availability](#4-scaling--availability)
5. [Payment Gateway Integration](#5-payment-gateway-integration)
6. [Monetization Strategy](#6-monetization-strategy)
7. [Security - OWASP Top 10](#7-security---owasp-top-10)
8. [Compliance](#8-compliance)

---

## 1. Functional Requirements Implementation

### 1.1 Read Scenarios (Browse & Search)

| Feature | Endpoint | Implementation |
|---------|----------|----------------|
| Browse theatres by city | `GET /api/theatres?city={city}` | TheatreController |
| Shows by movie, city, date | `GET /api/shows?movieTitle=&city=&date=` | ShowController |
| Show timings for theatre | `GET /api/shows?theatreId=&date=` | ShowService |
| Available seats | `GET /api/shows/{showId}/seats` | SeatController |
| Active offers | `GET /api/offers?activeOnly=true` | OfferController |

### 1.2 Write Scenarios (Booking & Management)

| Feature | Endpoint | Implementation |
|---------|----------|----------------|
| Book tickets | `POST /api/bookings` | BookingController |
| Confirm booking (payment) | `POST /api/bookings/{id}/confirm` | BookingController |
| Cancel booking | `POST /api/bookings/{id}/cancel` | BookingController |
| Bulk booking | `POST /api/bookings/bulk` | BookingController |
| Bulk cancellation | `POST /api/bookings/cancel-bulk` | BookingController |
| Create/Update/Delete shows | `POST/PUT/DELETE /api/shows` | ShowController |
| Seat inventory allocation | `POST /api/shows/{showId}/seats` | SeatController |

### 1.3 Offer Implementation

**Configured Offers (via application.yml):**
```yaml
bookmyshow:
  offers:
    enabled-cities: Mumbai,Delhi,Bangalore
    enabled-theatres: PVR Andheri,INOX Nariman Point
    afternoon-start: "12:00"
    afternoon-end: "17:59"
    third-ticket-discount-rate: 0.50      # 50% discount
    afternoon-show-discount-rate: 0.20    # 20% discount
```

**Database-backed Offers:**
- Create: `POST /api/offers`
- Update: `PUT /api/offers/{uuid}`
- Delete: `DELETE /api/offers/{uuid}` (soft delete)
- Offer types: THIRD_TICKET_DISCOUNT, AFTERNOON_SHOW, CITY_SPECIFIC, PROMO_CODE

---

## 2. Transactional Scenarios & Design Decisions

### 2.1 Booking Transaction Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         BOOKING TRANSACTION                             │
├─────────────────────────────────────────────────────────────────────────┤
│  1. Validate Request                                                    │
│     ├── Show exists & ACTIVE                                           │
│     ├── Seats available                                                │
│     ├── Seat limit (max 10 per booking)                                │
│     └── User booking limit (max 5 per day)                            │
│                                                                         │
│  2. Lock Seats (PESSIMISTIC_WRITE)                                     │
│     ├── SELECT FOR UPDATE on seats                                     │
│     └── Check seat status = AVAILABLE                                  │
│                                                                         │
│  3. Calculate Pricing                                                  │
│     ├── Base price × number of seats                                   │
│     ├── Apply discounts (50% third ticket, 20% afternoon)             │
│     ├── Calculate platform commission                                  │
│     └── Calculate GST                                                   │
│                                                                         │
│  4. Create Booking (PENDING status)                                    │
│     ├── Save booking with all pricing details                          │
│     ├── Update seat status to BOOKED                                   │
│     └── Save seat-hold expiry (5 minutes)                              │
│                                                                         │
│  5. Commit Transaction                                                 │
│     └── On failure: Rollback all changes                               │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Transactional Design Decisions

| Scenario | Isolation Level | Strategy |
|----------|-----------------|----------|
| Seat Booking | READ_COMMITTED | PESSIMISTIC_WRITE locking |
| Booking Confirmation | REPEATABLE_READ | Optimistic locking with @Version |
| Payment Processing | REPEATABLE_READ | Saga pattern (compensation) |
| Bulk Operations | READ_COMMITTED | Individual transactions per item |

### 2.3 Concurrency Handling

```java
// Pessimistic Locking for Seat Selection
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.show.uuid = :showUuid AND s.seatNumber IN :seatNumbers")
List<Seat> findByShowUuidAndSeatNumbersWithLock(@Param("showUuid") String showUuid, 
                                                 @Param("seatNumbers") Set<String> seatNumbers);

// Optimistic Locking for Booking
@Entity
public class Booking {
    @Version
    private Long version;
}
```

### 2.4 Saga Pattern for Payment

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Booking    │───>│   Payment    │───>│ Confirmation │
│  (Pending)   │    │  (Captured)  │    │  (Confirmed) │
└──────────────┘    └──────────────┘    └──────────────┘
       │                   │                    │
       │                   │                    │
       v                   v                    v
  Compensate:         Compensate:          Success:
  Release seats       Refund payment       Final state
```

---

## 3. Theatre Integration Architecture

### 3.1 Integration Types Supported

| Type | Protocol | Use Case |
|------|----------|----------|
| NEW | REST API | Modern theatres with IT systems |
| LEGACY_REST | REST (legacy) | Old REST endpoints |
| LEGACY_SOAP | SOAP | Old SOAP-based systems |
| LEGACY_FILE | File (CSV/XML) | Batch file processing |

### 3.2 Integration Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      THEATRE INTEGRATION LAYER                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────┐    ┌──────────────────┐    ┌─────────────────┐  │
│  │   Gateway   │    │   Adapter Layer  │    │ Integration     │  │
│  │   Factory   │───>│                  │───>│ Service         │  │
│  └─────────────┘    │  - REST Adapter  │    │                 │  │
│                     │  - SOAP Adapter  │    │ - Sync Shows    │  │
│                     │  - File Adapter  │    │ - Sync Inventory│  │
│                     │  - CSV Parser   │    │ - Sync Bookings │  │
│                     └──────────────────┘    └─────────────────┘  │
│                                    │                    │         │
│                                    v                    v         │
│                     ┌──────────────────────────────────────────┐  │
│                     │           Theatre Integration Service     │  │
│                     └──────────────────────────────────────────┘  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 Integration Implementation

```java
public interface TheatreAdapter {
    ShowInventory fetchInventory(String theatreId, LocalDate date);
    void updateShow(Show show);
    void syncBooking(Booking booking);
}

@Component
public class RestTheatreAdapter implements TheatreAdapter {
    // For NEW and LEGACY_REST
}

@Component
public class SoapTheatreAdapter implements TheatreAdapter {
    // For LEGACY_SOAP
}

@Component  
public class FileTheatreAdapter implements TheatreAdapter {
    // For LEGACY_FILE
}
```

### 3.4 Localization Support

```java
@Configuration
@ConfigurationProperties(prefix = "bookmyshow.localization")
public class LocalizationProperties {
    private String defaultLocale;        // en-US
    private List<String> supportedLocales; // [en-US, hi-IN, ta-IN, ...]
}

@Service
public class LocalizationService {
    // Movie title translations
    // Theatre name translations
    // Error message localization
    // Date/time format per locale
}
```

---

## 4. Scaling & Availability

### 4.1 Multi-Region Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     GLOBAL SCALING ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    CDN (CloudFlare/Akamai)                   │   │
│  │              Static Assets, Images, JS, CSS                  │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                    │                                 │
│                                    v                                 │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │              Load Balancer (AWS ALB / Azure LB)               │   │
│  │         Health Checks, SSL Termination, Routing              │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                    │                                 │
│         ┌──────────────────────────┼──────────────────────────┐    │
│         │                          │                          │    │
│         v                          v                          v    │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐│
│  │   Mumbai    │           │   Delhi     │           │  Bangalore  ││
│  │   Region    │           │   Region    │           │   Region    ││
│  │             │           │             │           │             ││
│  │  ┌───────┐  │           │  ┌───────┐  │           │  ┌───────┐  ││
│  │  │ App   │  │           │  │ App   │  │           │  │ App   │  ││
│  │  │Cluster│  │           │  │Cluster│  │           │  │Cluster│  ││
│  │  └───────┘  │           │  └───────┘  │           │  └───────┘  ││
│  │  ┌───────┐  │           │  ┌───────┐  │           │  ┌───────┐  ││
│  │  │  DB   │  │           │  │  DB   │  │           │  │  DB   │  ││
│  │  │(Read  │  │           │  │(Read  │  │           │  │(Read  │  ││
│  │  │ Replica)│           │  │ Replica)│           │  │ Replica)│ ││
│  │  └───────┘  │           │  └───────┘  │           │  └───────┘  ││
│  └─────────────┘           └─────────────┘           └─────────────┘│
│         │                          │                          │      │
│         └──────────────────────────┼──────────────────────────┘      │
│                                    │                                 │
│                                    v                                 │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │              Global Database (CockroachDB / Spanner)         │   │
│  │               Multi-region active-active replication          │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 4.2 Auto-Scaling Configuration

```yaml
bookmyshow:
  scaling:
    min-instances: 2
    max-instances: 10
    target-cpu-utilization: 70

# Kubernetes HPA Example
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: bookmyshow-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: bookmyshow
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### 4.3 Availability Calculations

| Component | Availability | Downtime/Year |
|-----------|--------------|---------------|
| Single Instance | 99.0% | 87.6 hours |
| Multi-AZ (3 AZ) | 99.99% | 52.6 minutes |
| Multi-Region | 99.999% | 5.26 minutes |
| CDN + Multi-Region | 99.9999% | 31.5 seconds |

**Achieving 99.99% (53 min/year downtime):**
- Deploy across 3 Availability Zones
- Use Application Load Balancer with health checks
- Implement circuit breakers
- Database read replicas for read scaling
- Redis cluster for caching
- Asynchronous processing for non-critical operations

---

## 5. Payment Gateway Integration

### 5.1 Supported Payment Gateways

| Gateway | Status | Features |
|---------|--------|----------|
| Stripe | ✅ Implemented | Cards, Wallets, UPI |
| Razorpay | ✅ Implemented | Cards, Wallets, UPI, EMI |
| Adyen | ✅ Implemented | Global payments |

### 5.2 Payment Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      PAYMENT GATEWAY INTEGRATION                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐                                                   │
│  │   Payment    │                                                   │
│  │   Service    │                                                   │
│  └──────┬───────┘                                                   │
│         │                                                            │
│         v                                                            │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              PaymentGateway Interface                        │  │
│  │  - processPayment(PaymentRequest) -> PaymentResult           │  │
│  │  - refundPayment(paymentId, amount) -> PaymentResult          │  │
│  │  - verifyPayment(paymentId) -> PaymentResult                 │  │
│  └──────────────────────────────────────────────────────────────┘  │
│         │           │           │                                   │
│         v           v           v                                   │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐                          │
│  │  Stripe   │ │  Razorpay │ │   Adyen   │                          │
│  │  Gateway  │ │  Gateway  │ │  Gateway  │                          │
│  └───────────┘ └───────────┘ └───────────┘                          │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              PaymentGatewayConfig (Factory)                   │  │
│  │  @Bean PaymentGateway() {                                    │  │
│  │      switch(gateway) {                                       │  │
│  │          case "stripe": return new StripePaymentGateway();   │  │
│  │          case "razorpay": return new RazorpayGateway();      │  │
│  │          case "adyen": return new AdyenGateway();            │  │
│  │      }                                                       │  │
│  │  }                                                           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 5.3 Payment Flow

```java
@Service
public class PaymentService {
    public PaymentResult processPayment(PaymentRequest request) {
        // 1. Validate request
        // 2. Process via selected gateway
        // 3. Handle webhook for async updates
        // 4. Return result
    }
}
```

---

## 6. Monetization Strategy

### 6.1 Revenue Streams

```yaml
bookmyshow:
  monetization:
    platform-commission-percent: 10   # Platform takes 10%
    gst-rate: 18                       # 18% GST on booking
```

| Revenue Stream | Model | Percentage |
|---------------|-------|------------|
| Platform Commission | Per booking | 10% of ticket price |
| Convenience Fee | Per transaction | ₹20-50 |
| Advertising | Display ads | Variable |
| Premium Listings | theatres pay | Monthly subscription |
| Data Analytics | Insights | Enterprise pricing |

### 6.2 Commission Structure

```
Ticket Price: ₹500
├── Base Price: ₹500
├── Platform Commission (10%): ₹50
├── GST (18% on ₹500): ₹90
└── Total: ₹590 (Customer pays)

Theatre Receives: ₹500 - ₹50 = ₹450
Platform Earns: ₹50 + convenience fee
```

---

## 7. Security - OWASP Top 10

### 7.1 Security Implementation

| OWASP Threat | Mitigation | Implementation |
|--------------|------------|----------------|
| **A01: Broken Access Control** | Role-based access, JWT validation | Spring Security + JWT filters |
| **A02: Cryptographic Failures** | TLS 1.3, AES-256 | SSL/TLS configuration |
| **A03: Injection** | Input validation, Parameterized queries | Hibernate ORM, input sanitization |
| **A04: Insecure Design** | Threat modeling, Code review | Security architecture |
| **A05: Security Misconfiguration** | Hardening, Scan automation | Regular penetration testing |
| **A06: Vulnerable Components** | Dependency scanning | OWASP Dependency-Check |
| **A07: Auth Failures** | MFA, Password policies | Spring Security |
| **A08: Data Integrity Failures** | Digital signatures | Database constraints |
| **A09: Logging Failures** | Centralized logging | ELK Stack, SIEM integration |
| **A10: SSRF** | URL validation, Allowlists | Input validation |

### 7.2 JWT Security Implementation

```java
@Configuration
@ConfigurationProperties(prefix = "bookmyshow.security")
public class SecurityProperties {
    private String jwtSecret;
    private long jwtExpirationMs;  // 24 hours
}

@Component
public class JwtTokenProvider {
    // Token generation with expiry
    // Token validation
    // Role extraction
}
```

### 7.3 Rate Limiting

```java
@Service
public class RateLimitingService {
    // Per-user rate limiting
    // Per-IP rate limiting
    // Circuit breaker pattern
}
```

---

## 8. Compliance

### 8.1 Implemented Compliance Features

```yaml
bookmyshow:
  compliance:
    data-retention-days: 365       # 1 year retention
    enable-audit-logging: true    # Full audit trail
```

| Compliance | Implementation |
|------------|----------------|
| **GDPR** | Data retention policies, Right to erasure |
| **PCI-DSS** | Tokenization, Secure payment handling |
| **SOC 2** | Audit logging, Access controls |
| **Data Privacy** | Encryption at rest, Anonymization |

### 8.2 Audit Logging

```java
@Service
public class AuditService {
    @EventListener
    public void handleBookingEvent(BookingEvent event) {
        // Log all booking operations
        // Store in audit table
        // Integrate with SIEM
    }
}
```

### 8.3 Data Retention

```
┌─────────────────────────────────────────────────────────────┐
│                    DATA RETENTION POLICY                    │
├─────────────────────────────────────────────────────────────┤
│  Data Type              │  Retention   │  Disposal        │
├─────────────────────────┼──────────────┼──────────────────┤
│  Booking Records        │  1 year      │  Anonymize       │
│  Payment Records        │  7 years     │  Secure delete   │
│  User Profiles          │  Until       │  Full erasure    │
│                         │  deletion     │                  │
│  Audit Logs             │  3 years     │  Archive/Delete  │
│  Session Data           │  24 hours    │  Auto-delete     │
│  Logs                   │  90 days     │  Auto-delete     │
└─────────────────────────┴──────────────┴──────────────────┘
```

---

## Summary

| Category | Implementation |
|----------|----------------|
| **Functional** | ✅ Booking, Shows, Theatres, Offers, Bulk operations |
| **Transactional** | ✅ Pessimistic locking, Optimistic locking, Saga pattern |
| **Integration** | ✅ REST, SOAP, File-based adapters |
| **Scaling** | ✅ Multi-AZ, Auto-scaling, Global DB |
| **Availability** | ✅ 99.99% target |
| **Payments** | ✅ Stripe, Razorpay, Adyen |
| **Monetization** | ✅ 10% commission, GST handling |
| **Security** | ✅ JWT, Rate limiting, OWASP compliance |
| **Compliance** | ✅ GDPR, Audit logging, Data retention |
