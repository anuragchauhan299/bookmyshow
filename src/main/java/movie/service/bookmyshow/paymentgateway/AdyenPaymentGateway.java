package movie.service.bookmyshow.paymentgateway;

import lombok.extern.slf4j.Slf4j;
import movie.service.bookmyshow.constant.AppConstants;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class AdyenPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Adyen payment for: {}", request.getBookingReference());

        String paymentId = AppConstants.Payment.ID_PREFIX_ADYEN + UUID.randomUUID().toString().substring(0, 16);

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status(AppConstants.Payment.STATUS_CAPTURED)
                .message(AppConstants.Payment.MESSAGE_PAYMENT_SUCCESS + "Adyen")
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Processing Adyen refund for: {} amount: {}", paymentId, amount);

        String refundId = AppConstants.Payment.ID_PREFIX_REFUND + UUID.randomUUID().toString().substring(0, 16);

        return PaymentResult.builder()
                .success(true)
                .paymentId(refundId)
                .status(AppConstants.Payment.STATUS_REFUNDED)
                .message(AppConstants.Payment.MESSAGE_REFUND_SUCCESS + "Adyen")
                .gatewayResponse("{\"id\":\"" + refundId + "\",\"resultCode\":\"Refunded\"}")
                .build();
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        log.info("Verifying Adyen payment: {}", paymentId);

        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .status(AppConstants.Payment.STATUS_VERIFIED)
                .message(AppConstants.Payment.MESSAGE_VERIFIED_SUCCESS)
                .gatewayResponse("{\"id\":\"" + paymentId + "\",\"resultCode\":\"Authorised\"}")
                .build();
    }
}
