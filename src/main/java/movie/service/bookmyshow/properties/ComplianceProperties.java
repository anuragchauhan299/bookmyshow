package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.compliance")
@Getter
@Setter
public class ComplianceProperties {
    private int dataRetentionDays;
    private boolean enableAuditLogging;
}
