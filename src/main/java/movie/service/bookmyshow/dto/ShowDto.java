package movie.service.bookmyshow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDto {
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal ticketPrice;
    private Long theatreId;
    private Long movieId;
}
