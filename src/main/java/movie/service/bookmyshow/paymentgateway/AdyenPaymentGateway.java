package movie.service.bookmyshow.paymentgateway;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class AdyenPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Adyen payment for: {}", request.getBookingReference());

        String paymentId = "adyen_" + UUID.randomUUID().toString().substring(0, 16);

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status("captured")
                .message("Payment successful via Adyen")
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Adyen refund for: {} amount: {}", paymentId, amount);

        String refundId = "refund_" + UUID.randomUUID().toString().substring(0, 16);

        return PaymentResult.builder()
                .success(true)
                .paymentId(refundId)
                .status("refunded")
                .message("Refund processed successfully via Adyen")
                .gatewayResponse("{\"id\":\"" + refundId + "\",\"resultCode\":\"Refunded\"}")
                .build();
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Adyen payment: {}", paymentId);

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status("verified")
                .message("Payment verified successfully")
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}")
                .build();
    }
}
