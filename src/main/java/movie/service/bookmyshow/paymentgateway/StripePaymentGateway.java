package movie.service.bookmyshow.paymentgateway;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class StripePaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Stripe payment for: {}", request.getBookingReference());

        String paymentId = "stripe_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        return new PaymentResult(
                true,
                paymentId,
                "captured",
                "Payment successful via Stripe",
                "{\"id\":\"" + paymentId + "\",\"status\":\"succeeded\"}"
        );
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Stripe refund for: {} amount: {}", paymentId, amount);

        String refundId = "refund_" + UUID.randomUUID().toString().substring(0, 16);

        return new PaymentResult(
                true,
                refundId,
                "refunded",
                "Refund processed successfully via Stripe",
                "{\"id\":\"" + refundId + "\",\"status\":\"succeeded\"}"
        );
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Stripe payment: {}", paymentId);

        return new PaymentResult(
                true,
                paymentId,
                "verified",
                "Payment verified successfully",
                "{\"id\":\"" + paymentId + "\",\"status\":\"succeeded\"}"
        );
    }
}
