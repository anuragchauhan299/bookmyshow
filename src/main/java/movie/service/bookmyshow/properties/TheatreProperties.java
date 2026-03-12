package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.theatre")
@Getter
@Setter
public class TheatreProperties {
    private int integrationTimeoutMs;
    private int maxRetryAttempts;
}
