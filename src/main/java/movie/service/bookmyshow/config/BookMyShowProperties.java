package movie.service.bookmyshow.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.properties.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow")
@Getter
@RequiredArgsConstructor
public class BookMyShowProperties {

    private final OffersProperties offers;
    private final BookingProperties booking;
    private final PaymentProperties payment;
    private final TheatreProperties theatre;
    private final SecurityProperties security;
    private final ScalingProperties scaling;
    private final LocalizationProperties localization;
    private final MonetizationProperties monetization;
    private final ComplianceProperties compliance;
}
