package movie.service.bookmyshow.controller;

import movie.service.bookmyshow.entity.*;
import movie.service.bookmyshow.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final ShowService showService;
    private final BookingService bookingService;
    private final TheatreIntegrationService theatreIntegrationService;
    private final LocalizationService localizationService;
    private final PaymentService paymentService;

    @GetMapping("/shows")
    public List<Show> getShows(@RequestParam(required = false) String movieTitle,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) LocalDate date) {
        return showService.findShows(movieTitle, city, date);
    }

    @PostMapping("/shows")
    public ResponseEntity<Show> createShow(@RequestBody ShowRequest request) {
        Show show = showService.createShow(mapToShow(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(show);
    }

    @PutMapping("/shows/{id}")
    public Show updateShow(@PathVariable String id, @RequestBody ShowRequest request) {
        return showService.updateShow(id, mapToShow(request));
    }

    @DeleteMapping("/shows/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShow(@PathVariable String id) {
        showService.deleteShow(id);
    }

    @PostMapping("/shows/{id}/seats")
    public void initializeSeats(@PathVariable String id, @RequestBody SeatInventoryRequest request) {
        theatreIntegrationService.initializeSeatInventory(id, request.rows(), request.seatsPerRow());
    }

    @GetMapping("/shows/{id}/seats")
    public List<Seat> getAvailableSeats(@PathVariable String id) {
        return bookingService.getAvailableSeats(id);
    }

    @PostMapping("/bookings")
    public ResponseEntity<Booking> bookTickets(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.showId(),
                request.seats(),
                request.userId(),
                request.userEmail(),
                request.userPhone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PostMapping("/bookings/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable String id, @RequestBody PaymentRequest request) {
        PaymentService.PaymentResult result = paymentService.processPayment(
                new PaymentService.PaymentRequest(
                        "",
                        request.amount(),
                        "INR",
                        request.email(),
                        request.phone(),
                        Map.of("bookingId", id)
                )
        );

        if (result.isSuccess()) {
            Booking booking = bookingService.confirmBooking(id, result.getPaymentId(), "stripe");
            return ResponseEntity.ok(booking);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String id, @RequestBody CancellationRequest request) {
        Booking booking = bookingService.cancelBooking(id, request.reason());
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/bookings/{id}")
    public Booking getBooking(@PathVariable String id) {
        return bookingService.getBooking(id);
    }

    @GetMapping("/cities")
    public List<City> getCities(@RequestParam(required = false) String country) {
        if (country != null) {
            return localizationService.getCitiesByCountry(country);
        }
        return localizationService.getActiveCities();
    }

    @GetMapping("/movies")
    public List<Movie> getMovies(@RequestParam(required = false) String city,
                                  @RequestParam(required = false, defaultValue = "en-US") String locale) {
        if (city != null) {
            return localizationService.getMoviesNowShowingInCity(city, locale);
        }
        return List.of();
    }

    @GetMapping("/theatres")
    public List<Theatre> getTheatres(@RequestParam String city) {
        return theatreIntegrationService.getTheatresByCity(city);
    }

    @PostMapping("/theatres")
    public ResponseEntity<Theatre> registerTheatre(@RequestBody TheatreRequest request) {
        Theatre theatre = Theatre.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .type(request.type() != null ? request.type() : Theatre.TheatreType.MULTIPLEX)
                .integrationType(request.integrationType() != null ? request.integrationType() : Theatre.IntegrationType.NEW)
                .integrationEndpoint(request.integrationEndpoint())
                .screenCount(request.screenCount())
                .totalSeats(request.totalSeats())
                .active(true)
                .build();
        
        Theatre saved = theatreIntegrationService.registerTheatre(theatre);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private Show mapToShow(ShowRequest request) {
        return Show.builder()
                .showDate(request.showDate())
                .showTime(request.showTime())
                .ticketPrice(request.ticketPrice())
                .build();
    }

    public record ShowRequest(LocalDate showDate, LocalTime showTime, 
                               java.math.BigDecimal ticketPrice, Long theatreId, Long movieId) {}

    public record SeatInventoryRequest(int rows, int seatsPerRow) {}

    public record BookingRequest(String showId, List<String> seats, 
                                  String userId, String userEmail, String userPhone) {}

    public record PaymentRequest(java.math.BigDecimal amount, String email, String phone) {}

    public record CancellationRequest(String reason) {}

    public record TheatreRequest(String name, String address, String city, String state, String country,
                                  Theatre.TheatreType type, Theatre.IntegrationType integrationType,
                                  String integrationEndpoint, Integer screenCount, Integer totalSeats) {}
}
