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

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status("captured")
                .message("Payment successful via Razorpay")
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"status\":\"captured\"}")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Razorpay refund for: {} amount: {}", paymentId, amount);

        String refundId = "refund_" + UUID.randomUUID().toString().substring(0, 16);

        return PaymentResult.builder()
                .success(true)
                .paymentId(refundId)
                .status("refunded")
                .message("Refund processed successfully via Razorpay")
                .gatewayResponse("{\"id\":\"" + refundId + "\",\"status\":\"processed\"}")
                .build();
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Razorpay payment: {}", paymentId);

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status("verified")
                .message("Payment verified successfully")
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"status\":\"captured\"}")
                .build();
    }
}
