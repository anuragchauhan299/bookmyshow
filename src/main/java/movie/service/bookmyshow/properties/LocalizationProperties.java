package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.localization")
@Getter
@Setter
public class LocalizationProperties {
    private String defaultLocale;
    private List<String> supportedLocales;
}
