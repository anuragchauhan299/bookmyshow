package movie.service.bookmyshow.service;

import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.*;
import movie.service.bookmyshow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheatreIntegrationService {

    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookMyShowProperties properties;

    @Transactional
    public Theatre registerTheatre(Theatre theatre) {
        log.info("Registering new theatre: {}", theatre.getName());
        
        if (theatreRepository.existsByNameAndCity(theatre.getName(), theatre.getCity())) {
            throw new IllegalArgumentException("Theatre already exists in this city");
        }
        
        return theatreRepository.save(theatre);
    }

    @Transactional
    public Show createShowForTheatre(Long theatreId, Show show) {
        log.info("Creating show for theatre ID: {}", theatreId);
        
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new IllegalArgumentException("Theatre not found"));
        
        if (!theatre.getActive()) {
            throw new IllegalArgumentException("Theatre is not active");
        }
        
        show.setTheatre(theatre);
        show.setCity(theatre.getCity());
        show.setUuid(UUID.randomUUID().toString());
        show.setStatus(Show.ShowStatus.ACTIVE);
        
        Show savedShow = showRepository.save(show);
        
        if (theatre.getIntegrationType() == Theatre.IntegrationType.NEW) {
            syncShowToTheatre(theatre, savedShow);
        } else {
            pushShowToLegacySystem(theatre, savedShow);
        }
        
        return savedShow;
    }

    @Transactional
    public void initializeSeatInventory(String showUuid, int rows, int seatsPerRow) {
        log.info("Initializing seat inventory for show: {}", showUuid);
        
        Show show = showRepository.findByUuid(showUuid)
                .orElseThrow(() -> new IllegalArgumentException("Show not found"));
        
        List<Seat> seats = new ArrayList<>();
        String[] sections = {"PREMIUM", "STANDARD", "STANDARD"};
        BigDecimal[] multipliers = {BigDecimal.valueOf(1.5), BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.8)};
        
        for (int row = 0; row < rows; row++) {
            char rowLetter = (char) ('A' + row);
            int section = row < rows / 3 ? 0 : (row < 2 * rows / 3 ? 1 : 2);
            
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = Seat.builder()
                        .uuid(UUID.randomUUID().toString())
                        .show(show)
                        .seatNumber(rowLetter + String.valueOf(seatNum))
                        .row(String.valueOf(rowLetter))
                        .section("Section-" + (row / (rows / 3) + 1))
                        .type(Seat.SeatType.STANDARD)
                        .status(Seat.SeatStatus.AVAILABLE)
                        .priceMultiplier(multipliers[section])
                        .build();
                seats.add(seat);
            }
        }
        
        seatRepository.saveAll(seats);
        
        show.setTotalSeats(seats.size());
        show.setAvailableSeats(seats.size());
        showRepository.save(show);
        
        log.info("Created {} seats for show {}", seats.size(), showUuid);
    }

    public void syncShowToTheatre(Theatre theatre, Show show) {
        log.info("Syncing show {} to new theatre {}", show.getId(), theatre.getName());
        
        if (theatre.getIntegrationEndpoint() != null) {
            try {
                // Integration logic for new theatres with API endpoint
                log.debug("Would sync to endpoint: {}", theatre.getIntegrationEndpoint());
            } catch (Exception e) {
                log.error("Failed to sync show to theatre: {}", e.getMessage());
            }
        }
    }

    public void pushShowToLegacySystem(Theatre theatre, Show show) {
        log.info("Pushing show {} to legacy theatre system for {}", show.getId(), theatre.getName());
        
        switch (theatre.getIntegrationType()) {
            case LEGACY_REST -> pushViaRest(theatre, show);
            case LEGACY_SOAP -> pushViaSoap(theatre, show);
            case LEGACY_FILE -> generateFileFeed(theatre, show);
            default -> log.warn("Unknown integration type: {}", theatre.getIntegrationType());
        }
    }

    private void pushViaRest(Theatre theatre, Show show) {
        log.debug("Pushing via REST to theatre: {}", theatre.getName());
    }

    private void pushViaSoap(Theatre theatre, Show show) {
        log.debug("Pushing via SOAP to theatre: {}", theatre.getName());
    }

    private void generateFileFeed(Theatre theatre, Show show) {
        log.debug("Generating file feed for theatre: {}", theatre.getName());
    }

    public void syncBookingToTheatre(Booking booking) {
        Theatre theatre = booking.getShow().getTheatre();
        
        if (theatre.getIntegrationType() == Theatre.IntegrationType.NEW) {
            syncShowToTheatre(theatre, booking.getShow());
        } else {
            pushBookingToLegacySystem(theatre, booking);
        }
    }

    private void pushBookingToLegacySystem(Theatre theatre, Booking booking) {
        log.info("Pushing booking {} to legacy system for theatre {}", 
                booking.getBookingReference(), theatre.getName());
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatreRepository.findByCityAndActive(city, true);
    }

    public List<Show> getShowsForTheatre(Long theatreId, LocalDate date) {
        return showRepository.findByTheatreIdAndShowDate(theatreId, date);
    }

    public Map<String, Object> getTheatreStatus(Long theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new IllegalArgumentException("Theatre not found"));
        
        Map<String, Object> status = new HashMap<>();
        status.put("theatreId", theatreId);
        status.put("theatreName", theatre.getName());
        status.put("integrationType", theatre.getIntegrationType());
        status.put("active", theatre.getActive());
        status.put("screenCount", theatre.getScreenCount());
        
        return status;
    }
}
