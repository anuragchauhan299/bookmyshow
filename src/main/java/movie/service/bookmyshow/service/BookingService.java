package movie.service.bookmyshow.service;

import movie.service.bookmyshow.model.TheatreShow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingService {

    private static final Set<String> OFFER_CITIES = Set.of("Mumbai", "Delhi", "Bangalore");

    private final List<TheatreShow> allShows = new ArrayList<>();

    public BookingService() {
        seedSampleShows();
    }

    /**
     * Browse theatres currently running the given movie in a city on a chosen date.
     */
    public List<TheatreShow> browseTheatres(String city, String movieTitle, LocalDate date) {
        return allShows.stream()
                .filter(show -> show.getCity().equalsIgnoreCase(city))
                .filter(show -> show.getMovieTitle().equalsIgnoreCase(movieTitle))
                .filter(show -> show.getShowDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Calculate total price applying:
     * - 50% discount on the third ticket
     * - 20% discount for afternoon shows
     * - Offers are only for selected cities.
     */
    public BigDecimal calculateTotalPrice(TheatreShow show, int numberOfTickets) {
        if (numberOfTickets <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal pricePerTicket = show.getTicketPrice();
        BigDecimal baseAmount = pricePerTicket.multiply(BigDecimal.valueOf(numberOfTickets));

        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Offers only in selected cities
        boolean offerCity = OFFER_CITIES.contains(show.getCity());

        if (offerCity && numberOfTickets >= 3) {
            // 50% discount on the third ticket
            BigDecimal thirdTicketDiscount = pricePerTicket.multiply(BigDecimal.valueOf(0.5));
            totalDiscount = totalDiscount.add(thirdTicketDiscount);
        }

        // Afternoon show discount: 20% on the whole booking
        if (isAfternoonShow(show.getShowTime())) {
            BigDecimal afternoonDiscount = baseAmount.multiply(BigDecimal.valueOf(0.20));
            totalDiscount = totalDiscount.add(afternoonDiscount);
        }

        BigDecimal finalAmount = baseAmount.subtract(totalDiscount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return finalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isAfternoonShow(LocalTime time) {
        // Example: afternoon between 12:00 (inclusive) and 17:59 (inclusive)
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(17, 59);
        return !time.isBefore(start) && !time.isAfter(end);
    }

    private void seedSampleShows() {
        allShows.add(new TheatreShow(
                "PVR Andheri",
                "Mumbai",
                "Inception",
                LocalDate.now(),
                LocalTime.of(14, 0),
                BigDecimal.valueOf(300)
        ));

        allShows.add(new TheatreShow(
                "INOX Nariman Point",
                "Mumbai",
                "Inception",
                LocalDate.now(),
                LocalTime.of(19, 30),
                BigDecimal.valueOf(350)
        ));

        allShows.add(new TheatreShow(
                "PVR Koramangala",
                "Bangalore",
                "Inception",
                LocalDate.now(),
                LocalTime.of(15, 0),
                BigDecimal.valueOf(280)
        ));
    }
}

