package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.offers")
@Getter
@Setter
public class OffersProperties {
    private Set<String> enabledCities;
    private Set<String> enabledTheatres;
    private String afternoonStart;
    private String afternoonEnd;
    private double thirdTicketDiscountRate;
    private double afternoonShowDiscountRate;
}
