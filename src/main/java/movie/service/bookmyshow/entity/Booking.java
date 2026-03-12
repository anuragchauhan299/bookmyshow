package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_show", columnList = "show_id"),
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_phone")
    private String userPhone;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "platform_commission", precision = 10, scale = 2)
    private BigDecimal platformCommission;

    @Column(name = "gst_amount", precision = 10, scale = 2)
    private BigDecimal gstAmount;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @ElementCollection
    @CollectionTable(name = "booking_applied_offers", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "offer_name")
    @Builder.Default
    private List<String> appliedOffers = new ArrayList<>();

    @Column(name = "booking_reference")
    private String bookingReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        if (bookingReference == null) {
            bookingReference = generateBookingReference();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    private String generateBookingReference() {
        return "BMS-" + System.currentTimeMillis();
    }

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, FAILED
    }

    public enum PaymentStatus {
        PENDING, AUTHORIZED, CAPTURED, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }
}
