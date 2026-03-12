package movie.service.bookmyshow.paymentgateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String bookingReference;
    private BigDecimal amount;
    private String currency;
    private String customerEmail;
    private String customerPhone;
    private Map<String, String> metadata;
}
