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

        return new PaymentResult(
                true,
                paymentId,
                "captured",
                "Payment successful via Adyen",
                "{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}"
        );
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Adyen refund for: {} amount: {}", paymentId, amount);

        String refundId = "refund_" + UUID.randomUUID().toString().substring(0, 16);

        return new PaymentResult(
                true,
                refundId,
                "refunded",
                "Refund processed successfully via Adyen",
                "{\"id\":\"" + refundId + "\",\"resultCode\":\"Refunded\"}"
        );
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Adyen payment: {}", paymentId);

        return new PaymentResult(
                true,
                paymentId,
                "verified",
                "Payment verified successfully",
                "{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}"
        );
    }
}
