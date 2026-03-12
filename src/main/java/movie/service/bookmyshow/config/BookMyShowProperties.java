package movie.service.bookmyshow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow")
@Getter
@Setter
public class BookMyShowProperties {

    private Offers offers = new Offers();
    private Booking booking = new Booking();
    private Payment payment = new Payment();
    private Theatre theatre = new Theatre();
    private Security security = new Security();
    private Scaling scaling = new Scaling();
    private Localization localization = new Localization();
    private Monetization monetization = new Monetization();
    private Compliance compliance = new Compliance();

    @Getter
    @Setter
    public static class Offers {
        private Set<String> enabledCities;
        private Set<String> enabledTheatres;
        private String afternoonStart;
        private String afternoonEnd;
        private double thirdTicketDiscountRate;
        private double afternoonShowDiscountRate;
    }

    @Getter
    @Setter
    public static class Booking {
        private int seatHoldDurationMinutes;
        private int maxSeatsPerBooking;
        private int maxBookingsPerUserPerDay;
    }

    @Getter
    @Setter
    public static class Payment {
        private String gateway;
        private int timeoutSeconds;
        private int retryAttempts;
    }

    @Getter
    @Setter
    public static class Theatre {
        private int integrationTimeoutMs;
        private int maxRetryAttempts;
    }

    @Getter
    @Setter
    public static class Security {
        private String jwtSecret;
        private long jwtExpirationMs;
    }

    @Getter
    @Setter
    public static class Scaling {
        private int minInstances;
        private int maxInstances;
        private int targetCpuUtilization;
    }

    @Getter
    @Setter
    public static class Localization {
        private String defaultLocale;
        private List<String> supportedLocales;
    }

    @Getter
    @Setter
    public static class Monetization {
        private double platformCommissionPercent;
        private double gstRate;
    }

    @Getter
    @Setter
    public static class Compliance {
        private int dataRetentionDays;
        private boolean enableAuditLogging;
    }
}
