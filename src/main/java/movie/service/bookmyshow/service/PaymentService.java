package movie.service.bookmyshow.service;

import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PaymentService {

    private final BookMyShowProperties properties;
    private final Map<String, PaymentGateway> gateways = new ConcurrentHashMap<>();

    public PaymentService(BookMyShowProperties properties) {
        this.properties = properties;
        initializeGateways();
    }

    private void initializeGateways() {
        String gatewayName = properties.getPayment().getGateway().toLowerCase();
        gateways.put("stripe", new StripePaymentGateway());
        gateways.put("razorpay", new RazorpayPaymentGateway());
        gateways.put("adyen", new AdyenPaymentGateway());
        
        log.info("Initialized payment gateway: {}", gatewayName);
    }

    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing payment for booking: {} amount: {}", 
                request.getBookingReference(), request.getAmount());
        
        String gatewayName = properties.getPayment().getGateway().toLowerCase();
        PaymentGateway gateway = gateways.get(gatewayName);
        
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + gatewayName);
        }
        
        return gateway.processPayment(request);
    }

    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing refund for payment: {} amount: {}", paymentId, amount);
        
        String gatewayName = properties.getPayment().getGateway().toLowerCase();
        PaymentGateway gateway = gateways.get(gatewayName);
        
        return gateway.refundPayment(paymentId, amount);
    }

    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying payment: {}", paymentId);
        
        String gatewayName = properties.getPayment().getGateway().toLowerCase();
        PaymentGateway gateway = gateways.get(gatewayName);
        
        return gateway.verifyPayment(paymentId);
    }

    public interface PaymentGateway {
        PaymentResult processPayment(PaymentRequest request);
        PaymentResult refundPayment(String paymentId, BigDecimal amount);
        PaymentResult verifyPayment(String paymentId);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PaymentRequest {
        private String bookingReference;
        private BigDecimal amount;
        private String currency;
        private String customerEmail;
        private String customerPhone;
        private Map<String, String> metadata;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PaymentResult {
        private boolean success;
        private String paymentId;
        private String status;
        private String message;
        private String gatewayResponse;
    }

    private static class StripePaymentGateway implements PaymentGateway {
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

    private static class RazorpayPaymentGateway implements PaymentGateway {
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

    private static class AdyenPaymentGateway implements PaymentGateway {
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
}
