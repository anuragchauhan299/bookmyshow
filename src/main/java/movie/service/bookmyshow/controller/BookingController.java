package movie.service.bookmyshow.controller;

import movie.service.bookmyshow.model.Booking;
import movie.service.bookmyshow.model.Show;
import movie.service.bookmyshow.service.ShowService;
import movie.service.bookmyshow.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final ShowService showService;
    private final TicketService ticketService;

    public BookingController(ShowService showService, TicketService ticketService) {
        this.showService = showService;
        this.ticketService = ticketService;
    }

    // Browse shows for a movie in a city on a given day
    @GetMapping("/shows")
    public List<Show> getShows(@RequestParam(required = false) String movieTitle,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) LocalDate date) {
        return showService.findShows(movieTitle, city, date);
    }

    // Theatres create shows
    @PostMapping("/shows")
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        Show created = showService.createShow(show);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Theatres update shows
    @PutMapping("/shows/{id}")
    public Show updateShow(@PathVariable String id, @RequestBody Show show) {
        return showService.updateShow(id, show);
    }

    // Theatres delete shows
    @DeleteMapping("/shows/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShow(@PathVariable String id) {
        showService.deleteShow(id);
    }

    // Allocate or update seat inventory for a show
    @PostMapping("/shows/{id}/seats")
    public Show allocateSeats(@PathVariable String id, @RequestBody Set<String> seatIds) {
        return showService.allocateSeats(id, seatIds);
    }

    // Book movie tickets by selecting show and preferred seats
    @PostMapping("/bookings")
    public Booking bookTickets(@RequestBody BookingRequest request) {
        return ticketService.bookTickets(request.showId(), request.seats());
    }

    // Bulk booking for a show
    @PostMapping("/bookings/bulk")
    public List<Booking> bulkBook(@RequestBody BulkBookingRequest request) {
        return ticketService.bulkBookTickets(request.showId(), request.seatGroups());
    }

    // Cancel a booking
    @PostMapping("/bookings/{id}/cancel")
    public Booking cancelBooking(@PathVariable String id) {
        return ticketService.cancelBooking(id);
    }

    // Bulk cancellation
    @PostMapping("/bookings/cancel-bulk")
    public List<Booking> bulkCancel(@RequestBody Map<String, List<String>> body) {
        List<String> bookingIds = body.get("bookingIds");
        return ticketService.bulkCancelBookings(bookingIds);
    }

    public record BookingRequest(String showId, List<String> seats) {}

    public record BulkBookingRequest(String showId, List<List<String>> seatGroups) {}
}

