package movie.service.bookmyshow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    private String uuid;
    private String code;
    private String name;
    private String description;
    private String type;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minBookingAmount;
    private Integer minTicketsRequired;
    private Integer maxUsageTotal;
    private Integer maxUsagePerUser;
    private Set<String> applicableCities;
    private Set<String> applicableTheatres;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Integer priority;
}
