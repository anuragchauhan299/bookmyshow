package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.payment")
@Getter
@Setter
public class PaymentProperties {
    private String gateway;
    private int timeoutSeconds;
    private int retryAttempts;
    private List<String> enabledGateways;
}
