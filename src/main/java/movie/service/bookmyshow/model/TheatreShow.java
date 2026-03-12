package movie.service.bookmyshow.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TheatreShow {

    private final String theatreName;
    private final String city;
    private final String movieTitle;
    private final LocalDate showDate;
    private final LocalTime showTime;
    private final BigDecimal ticketPrice;

    public TheatreShow(String theatreName,
                       String city,
                       String movieTitle,
                       LocalDate showDate,
                       LocalTime showTime,
                       BigDecimal ticketPrice) {
        this.theatreName = theatreName;
        this.city = city;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.ticketPrice = ticketPrice;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public String getCity() {
        return city;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }
}

