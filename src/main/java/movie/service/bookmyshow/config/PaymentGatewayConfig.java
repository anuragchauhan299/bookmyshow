package movie.service.bookmyshow.config;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.paymentgateway.AdyenPaymentGateway;
import movie.service.bookmyshow.paymentgateway.PaymentGateway;
import movie.service.bookmyshow.paymentgateway.RazorpayPaymentGateway;
import movie.service.bookmyshow.paymentgateway.StripePaymentGateway;
import movie.service.bookmyshow.properties.PaymentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PaymentGatewayConfig {

    private final PaymentProperties paymentProperties;

    @Bean
    public PaymentGateway paymentGateway() {
        String gateway = paymentProperties.getGateway();
        return switch (gateway.toLowerCase()) {
            case "razorpay" -> new RazorpayPaymentGateway();
            case "adyen" -> new AdyenPaymentGateway();
            case "stripe" -> new StripePaymentGateway();
            default -> new StripePaymentGateway();
        };
    }
}
