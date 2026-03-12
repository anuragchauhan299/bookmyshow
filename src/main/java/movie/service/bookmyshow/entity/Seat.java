package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_seat_show", columnList = "show_id"),
    @Index(name = "idx_seat_show_status", columnList = "show_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false)
    private String seatNumber;

    @Column(name = "seat_row")
    private String row;

    @Column(nullable = false)
    private String section;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column(name = "price_multiplier", precision = 5, scale = 2)
    private BigDecimal priceMultiplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "hold_expiry")
    private java.time.LocalDateTime holdExpiry;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        if (status == null) {
            status = SeatStatus.AVAILABLE;
        }
        if (priceMultiplier == null) {
            priceMultiplier = BigDecimal.ONE;
        }
    }

    public enum SeatType {
        STANDARD, PREMIUM, VIP, ACCESSIBLE
    }

    public enum SeatStatus {
        AVAILABLE, HELD, BOOKED, RESERVED
    }
}
