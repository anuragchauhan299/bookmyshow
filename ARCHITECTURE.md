# BookMyShow Platform - Architecture Documentation

## 1. Transactional Scenarios and Design Decisions

### 1.1 Seat Reservation Transaction
```
Scenario: User selects seats and books tickets
-------------------------------------------------------------
1. Validate Show Exists & Active
   → Check show status = ACTIVE, date not passed
   
2. Validate Seat Availability (Pessimistic Lock)
   → SELECT ... FOR UPDATE on seats
   → Check status = AVAILABLE
   → Check no valid hold (hold_expiry < now)
   
3. Reserve Seats
   → UPDATE seats SET status = BOOKED, booking_id = ?
   → All seats in single transaction
   
4. Calculate Pricing
   → Base price × ticket count
   → Apply discounts (third ticket 50%, afternoon 20%)
   → Calculate GST and platform commission
   
5. Create Booking
   → INSERT booking with PENDING status
   → Return booking reference
   
6. Payment Processing (Saga Pattern)
   → Process payment via gateway
   → On success: UPDATE status = CONFIRMED
   → On failure: Rollback seat reservation
```

### 1.2 Design Decisions

| Scenario | Decision | Rationale |
|----------|----------|-----------|
| Seat Locking | Pessimistic Locking (SELECT FOR UPDATE) | Prevents double booking at DB level |
| Booking Confirmation | Two-phase (PENDING → CONFIRMED) | Allows payment integration, prevents stock manipulation |
| Concurrent Bookings | Serializable isolation per show | Ensures consistency under high load |
| Seat Hold | 5-minute expiry with timestamp | Balances user experience vs inventory locking |

### 1.3 Transaction Boundaries
- **Single Booking**: Atomic - all seats succeed or fail together
- **Bulk Booking**: Best-effort partial success (individual seat-level transactions)
- **Payment Failure**: Automatic rollback with seat release

---

## 2. Theatre Integration (Legacy + New)

### 2.1 Integration Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    BookMyShow Platform                    │
├─────────────────────────────────────────────────────────┤
│  TheatreIntegrationService                               │
│  ├── NEW Theatres → REST API Push                       │
│  ├── LEGACY_REST → Adapter Pattern                      │
│  ├── LEGACY_SOAP → SOAP Client Wrapper                  │
│  └── LEGACY_FILE → File Feed Generator                  │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Supported Integration Types

| Type | Use Case | Implementation |
|------|----------|----------------|
| NEW | Modern theatres with API | Direct REST sync |
| LEGACY_REST | Older systems with REST endpoints | Adapter with retry logic |
| LEGACY_SOAP | Traditional POS systems | SOAP client wrapper |
| LEGACY_FILE | Mainframe/batch systems | CSV/XML file generation |

### 2.3 Localization (Movies)
- **Movie Metadata**: Stored with multiple locales (en-US, hi-IN, ta-IN, etc.)
- **Localized Titles/Descriptions**: Map<String, String> per movie
- **City-based Localization**: 
  - Timezone-aware show times
  - Currency per city (INR, USD, etc.)
  - Language preference per region

---

## 3. Scaling & Availability (99.99%)

### 3.1 Multi-City/Multi-Country Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    Global Load Balancer                  │
│                  (AWS Global Accelerator)               │
└─────────────────────┬───────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   ┌─────────┐  ┌─────────┐  ┌─────────┐
   │ Region  │  │ Region  │  │ Region  │
   │  (IN)   │  │  (US)  │  │  (UK)  │
   └────┬────┘  └────┬────┘  └────┬────┘
        │            │            │
   ┌────▼────┐  ┌────▼────┐  ┌────▼────┐
   │  K8s    │  │  K8s    │  │  K8s    │
   │ Cluster │  │ Cluster │  │ Cluster │
   └────┬────┘  └────┬────┘  └────┬────┘
        │            │            │
   ┌────▼────┐  ┌────▼────┐  ┌────▼────┐
   │PostgreSQL│  │PostgreSQL│  │PostgreSQL│
   │ (Multi-AZ)│  │(Multi-AZ)│  │(Multi-AZ)│
   └─────────┘  └─────────┘  └─────────┘
```

### 3.2 Availability Targets

| Component | Strategy | Availability |
|-----------|----------|--------------|
| Application | Auto-scaling K8s (2-10 pods) | 99.99% |
| Database | Multi-AZ PostgreSQL | 99.99% |
| Cache | Redis Cluster (Multi-AZ) | 99.99% |
| Payment Gateway | Circuit breaker + retry | 99.9% |
| Theatre Integration | Per-theatre isolation | 99.5% |

### 3.3 Scaling Configuration
```yaml
bookmyshow:
  scaling:
    min-instances: 2
    max-instances: 10
    target-cpu-utilization: 70
    
# Database Connection Pooling
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### 3.4 High Availability Features
- **Health Checks**: `/actuator/health` for load balancer
- **Graceful Degradation**: Per-theatre isolation prevents cascade failures
- **Circuit Breaker**: Payment gateway failures don't crash the platform
- **Blue-Green Deployments**: Zero-downtime releases

---

## 4. Payment Gateway Integration

### 4.1 Supported Gateways
- **Stripe** - International cards, wallets
- **Razorpay** - India-focused (UPI, cards, net banking)
- **Adyen** - Global enterprise

### 4.2 Payment Flow
```
User → Booking Created (PENDING)
    → Redirect to Payment Gateway
    → Gateway processes payment
    → Webhook/Callback → Confirm Booking (CAPTURED)
    → On failure → Cancel Booking (REFUNDED)
```

### 4.3 Security
- **Tokenization**: No card data touches our servers
- **PCI-DSS**: Hosted payment pages
- **Idempotency**: Prevent duplicate charges

---

## 5. Monetization

### 5.1 Revenue Model
```
┌─────────────────────────────────────────┐
│           Booking Total                  │
├─────────────────────────────────────────┤
│ Base Price × Tickets                     │
│ ──────────────────────────────────────  │
│ + Third Ticket Discount (50%)           │
│ + Afternoon Show Discount (20%)          │
│ ──────────────────────────────────────  │
│ = Subtotal                              │
│ + GST (18%)                             │
│ ──────────────────────────────────────  │
│ = Total Customer Pays                    │
├─────────────────────────────────────────┤
│ Platform Commission (10%) ← OUR REVENUE│
│ Theatre Payout (90%)                    │
└─────────────────────────────────────────┘
```

### 5.2 Commission Configuration
```yaml
bookmyshow:
  monetization:
    platform-commission-percent: 10
    gst-rate: 18
```

---

## 6. OWASP Top 10 Protection

### 6.1 Security Implementation

| OWASP Risk | Protection |
|------------|------------|
| A01:2021 - Broken Access Control | JWT validation, role-based endpoints |
| A02:2021 - Cryptographic Failures | AES-256 at rest, TLS 1.3 in transit |
| A03:2021 - Injection | Parameterized queries (JPA), input validation |
| A04:2021 - Insecure Design | Rate limiting, circuit breakers |
| A05:2021 - Security Misconfiguration | Hardened baselines, secret rotation |
| A06:2021 - Vulnerable Components | Dependency scanning, CVE monitoring |
| A07:2021 - Auth Failures | JWT expiry, account lockout |
| A08:2021 - Data Failures | Encryption, PII masking |
| A09:2021 - Logging Failures | Structured logs, correlation IDs |
| A10:2021 - SSRF | Allowlist, sanitized URLs |

### 6.2 Security Configuration
```yaml
bookmyshow:
  security:
    jwt-secret: ${JWT_SECRET}  # Externalize!
    jwt-expiration-ms: 86400000
    
# All configs externalized - no hardcoded values
```

---

## 7. Compliance

### 7.1 Implemented Compliance

| Compliance | Implementation |
|------------|----------------|
| **GDPR/CCPA** | Data minimization, consent tracking, right-to-delete |
| **PCI-DSS** | Tokenized payments via gateway (we don't store cards) |
| **SOC 2** | Audit logs, encryption, access controls |

### 7.2 Audit Logging
```yaml
bookmyshow:
  compliance:
    enable-audit-logging: true
    data-retention-days: 365
```

All bookings, payments, and user actions are logged with:
- Correlation ID
- User ID/IP
- Before/After values
- Timestamp

---

## 8. API Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/shows` | GET | Browse shows (movie, city, date) |
| `/api/shows` | POST | Create show |
| `/api/shows/{id}` | PUT | Update show |
| `/api/shows/{id}` | DELETE | Cancel show |
| `/api/shows/{id}/seats` | POST | Initialize seats |
| `/api/bookings` | POST | Book tickets |
| `/api/bookings/bulk` | POST | Bulk booking |
| `/api/bookings/{id}/confirm` | POST | Confirm with payment |
| `/api/bookings/{id}/cancel` | POST | Cancel booking |
| `/api/bookings/cancel-bulk` | POST | Bulk cancellation |
| `/api/cities` | GET | List cities |
| `/api/movies` | GET | List movies (localized) |
| `/api/theatres` | GET | List theatres |

---

## 9. Configuration (All Externalized)

All values are configurable via environment variables or config files:

```bash
# Database
DATABASE_URL=jdbc:h2:mem:bookmyshow
DATABASE_USERNAME=sa
DATABASE_PASSWORD=

# Offers
OFFER_ENABLED_CITIES=Mumbai,Delhi,Bangalore
OFFER_AFTERNOON_START=12:00
OFFER_AFTERNOON_END=17:59

# Payment
PAYMENT_GATEWAY=stripe

# Security  
JWT_SECRET=your-secret-key

# Scaling
SCALING_MIN_INSTANCES=2
SCALING_MAX_INSTANCES=10
```
