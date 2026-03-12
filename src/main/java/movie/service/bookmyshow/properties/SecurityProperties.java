package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.security")
@Getter
@Setter
public class SecurityProperties {
    private String jwtSecret;
    private long jwtExpirationMs;
}
