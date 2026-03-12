package movie.service.bookmyshow.service;

import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.*;
import movie.service.bookmyshow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private void pushViaRest(Theatre theatre, Show show) {
        log.debug("Pushing via REST to theatre: {}", theatre.getName());
    }

    private void pushViaSoap(Theatre theatre, Show show) {
        log.debug("Pushing via SOAP to theatre: {}", theatre.getName());
    }

    private void generateFileFeed(Theatre theatre, Show show) {
        log.debug("Generating file feed for theatre: {}", theatre.getName());
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatreRepository.findByCityAndActive(city, true);
    }
}
