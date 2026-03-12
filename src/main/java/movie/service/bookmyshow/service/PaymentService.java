package movie.service.bookmyshow.service;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.paymentgateway.PaymentGateway;
import movie.service.bookmyshow.paymentgateway.PaymentRequest;
import movie.service.bookmyshow.paymentgateway.PaymentResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;

    public PaymentResult processPayment(PaymentRequest request) {
        return paymentGateway.processPayment(request);
    }
}
