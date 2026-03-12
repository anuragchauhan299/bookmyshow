package movie.service.bookmyshow.service;

import movie.service.bookmyshow.paymentgateway.PaymentGateway;
import movie.service.bookmyshow.paymentgateway.PaymentRequest;
import movie.service.bookmyshow.paymentgateway.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;

    public PaymentResult processPayment(PaymentRequest request) {
        return paymentGateway.processPayment(request);
    }

    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        return paymentGateway.refundPayment(paymentId, amount);
    }

    public PaymentResult verifyPayment(String paymentId) {
        return paymentGateway.verifyPayment(paymentId);
    }
}
