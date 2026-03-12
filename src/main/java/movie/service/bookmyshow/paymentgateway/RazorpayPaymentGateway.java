package movie.service.bookmyshow.paymentgateway;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class RazorpayPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Razorpay payment for: {}", request.getBookingReference());

        String paymentId = "razorpay_" + UUID.randomUUID().toString().substring(0, 16);

        return new PaymentResult(
                true,
                paymentId,
                "captured",
                "Payment successful via Razorpay",
                "{\"id\":\"" + paymentId + "\",\"status\":\"captured\"}"
        );
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Razorpay refund for: {} amount: {}", paymentId, amount);

        String refundId = "refund_" + UUID.randomUUID().toString().substring(0, 16);

        return new PaymentResult(
                true,
                refundId,
                "refunded",
                "Refund processed successfully via Razorpay",
                "{\"id\":\"" + refundId + "\",\"status\":\"processed\"}"
        );
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Razorpay payment: {}", paymentId);

        return new PaymentResult(
                true,
                paymentId,
                "verified",
                "Payment verified successfully",
                "{\"id\":\"" + paymentId + "\",\"status\":\"captured\"}"
        );
    }
}
