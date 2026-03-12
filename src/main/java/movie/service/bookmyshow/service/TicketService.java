package movie.service.bookmyshow.service;

import movie.service.bookmyshow.model.Booking;
import movie.service.bookmyshow.model.BookingStatus;
import movie.service.bookmyshow.model.Show;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final ShowService showService;
    private final Map<String, Booking> bookings = new HashMap<>();

    public TicketService(ShowService showService) {
        this.showService = showService;
    }

    public Booking bookTickets(String showId, List<String> seats) {
        Show show = showService.getShow(showId);
        showService.reserveSeats(showId, seats);

        BigDecimal totalPrice = show.getTicketPrice()
                .multiply(BigDecimal.valueOf(seats.size()));

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());
        booking.setShowId(showId);
        booking.setSeats(new ArrayList<>(seats));
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.BOOKED);

        bookings.put(booking.getId(), booking);
        return booking;
    }

    public List<Booking> bulkBookTickets(String showId, List<List<String>> seatGroups) {
        return seatGroups.stream()
                .map(seats -> bookTickets(showId, seats))
                .collect(Collectors.toList());
    }

    public Booking cancelBooking(String bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }
        showService.releaseSeats(booking.getShowId(), booking.getSeats());
        booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }

    public List<Booking> bulkCancelBookings(List<String> bookingIds) {
        return bookingIds.stream()
                .map(this::cancelBooking)
                .collect(Collectors.toList());
    }

    public Booking getBooking(String id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new NoSuchElementException("Booking not found: " + id);
        }
        return booking;
    }
}

