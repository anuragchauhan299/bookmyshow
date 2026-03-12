package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.dto.SeatInventoryDto;
import movie.service.bookmyshow.entity.Seat;
import movie.service.bookmyshow.service.BookingService;
import movie.service.bookmyshow.service.TheatreIntegrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows/{showId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final TheatreIntegrationService theatreIntegrationService;
    private final BookingService bookingService;

    @PostMapping
    public void initializeSeats(@PathVariable String showId, @RequestBody SeatInventoryDto dto) {
        theatreIntegrationService.initializeSeatInventory(showId, dto.getRows(), dto.getSeatsPerRow());
    }

    @GetMapping
    public List<Seat> getAvailableSeats(@PathVariable String showId) {
        return bookingService.getAvailableSeats(showId);
    }
}
