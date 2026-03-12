package movie.service.bookmyshow.config;

import lombok.Getter;
import movie.service.bookmyshow.properties.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow")
@Getter
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

    public BookMyShowProperties(
            OffersProperties offers,
            BookingProperties booking,
            PaymentProperties payment,
            TheatreProperties theatre,
            SecurityProperties security,
            ScalingProperties scaling,
            LocalizationProperties localization,
            MonetizationProperties monetization,
            ComplianceProperties compliance) {
        this.offers = offers;
        this.booking = booking;
        this.payment = payment;
        this.theatre = theatre;
        this.security = security;
        this.scaling = scaling;
        this.localization = localization;
        this.monetization = monetization;
        this.compliance = compliance;
    }
}
