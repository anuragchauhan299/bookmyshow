package movie.service.bookmyshow.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class Show {

    private String id;
    private String theatreName;
    private String city;
    private String movieTitle;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal ticketPrice;

    private Set<String> seatInventory = new HashSet<>();
    private Set<String> bookedSeats = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Set<String> getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(Set<String> seatInventory) {
        this.seatInventory = seatInventory;
    }

    public Set<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(Set<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}

