package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offers", indexes = {
    @Index(name = "idx_offer_code", columnList = "code", unique = true),
    @Index(name = "idx_offer_type", columnList = "type"),
    @Index(name = "idx_offer_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_booking_amount", precision = 10, scale = 2)
    private BigDecimal minBookingAmount;

    @Column(name = "min_tickets_required")
    private Integer minTicketsRequired;

    @Column(name = "max_usage_total")
    private Integer maxUsageTotal;

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;

    @Column(name = "current_usage_total")
    private Integer currentUsageTotal;

    @ElementCollection
    @CollectionTable(name = "offer_applicable_cities", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "city")
    @Builder.Default
    private Set<String> applicableCities = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "offer_applicable_theatres", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "theatre")
    @Builder.Default
    private Set<String> applicableTheatres = new HashSet<>();

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "priority")
    private Integer priority;

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = true;
        }
        if (currentUsageTotal == null) {
            currentUsageTotal = 0;
        }
        if (priority == null) {
            priority = 0;
        }
    }

    public enum OfferType {
        THIRD_TICKET_DISCOUNT, AFTERNOON_SHOW, CITY_SPECIFIC, THEATRE_SPECIFIC, PROMO_CODE, FESTIVE
    }

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }
}
