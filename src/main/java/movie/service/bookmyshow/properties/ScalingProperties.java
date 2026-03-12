package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.scaling")
@Getter
@Setter
public class ScalingProperties {
    private int minInstances;
    private int maxInstances;
    private int targetCpuUtilization;
}
