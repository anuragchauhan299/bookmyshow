package movie.service.bookmyshow.paymentgateway;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);

    PaymentResult refundPayment(String paymentId, BigDecimal amount);

    PaymentResult verifyPayment(String paymentId);
}
