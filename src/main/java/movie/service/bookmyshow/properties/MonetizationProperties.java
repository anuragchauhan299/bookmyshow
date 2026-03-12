package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.monetization")
@Getter
@Setter
public class MonetizationProperties {
    private double platformCommissionPercent;
    private double gstRate;
}
