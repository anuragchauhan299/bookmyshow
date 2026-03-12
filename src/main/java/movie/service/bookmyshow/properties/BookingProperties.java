package movie.service.bookmyshow.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookmyshow.booking")
@Getter
@Setter
public class BookingProperties {
    private int seatHoldDurationMinutes;
    private int maxSeatsPerBooking;
    private int maxBookingsPerUserPerDay;
}
