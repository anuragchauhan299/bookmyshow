package movie.service.bookmyshow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import movie.service.bookmyshow.constant.AppConstants;
import movie.service.bookmyshow.entity.Show;
import movie.service.bookmyshow.exception.ShowNotFoundException;
import movie.service.bookmyshow.repository.MovieRepository;
import movie.service.bookmyshow.repository.SeatRepository;
import movie.service.bookmyshow.repository.ShowRepository;
import movie.service.bookmyshow.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Show createShow(Show show) {
        log.info("Creating show for movie: {} at theatre: {}", show.getMovie().getId(), show.getTheatre().getId());

        show.setUuid(UUID.randomUUID().toString());
        show.setStatus(Show.ShowStatus.ACTIVE);

        return showRepository.save(show);
    }

    @Transactional
    public Show updateShow(String uuid, Show updatedShow) {
        log.info("Updating show: {}", uuid);

        Show existing = showRepository.findByUuid(uuid)
                .orElseThrow(() -> new ShowNotFoundException(AppConstants.ErrorMessage.SHOW_NOT_FOUND + uuid));

        existing.setShowDate(updatedShow.getShowDate());
        existing.setShowTime(updatedShow.getShowTime());
        existing.setTicketPrice(updatedShow.getTicketPrice());
        existing.setStatus(updatedShow.getStatus());

        return showRepository.save(existing);
    }

    public List<Show> findShows(String movieTitle, String city, LocalDate date) {
        if (movieTitle != null && city != null && date != null) {
            return showRepository.findByCityAndMovieTitleAndShowDate(city, movieTitle, date);
        } else if (movieTitle != null && city != null) {
            return showRepository.searchShows(city, movieTitle, LocalDate.now());
        } else if (city != null && date != null) {
            return showRepository.findByCityAndShowDate(city, date);
        }
        return showRepository.findAll();
    }

    @Transactional
    public void cancelShow(String uuid) {
        log.info("Cancelling show: {}", uuid);

        Show show = showRepository.findByUuid(uuid)
                .orElseThrow(() -> new ShowNotFoundException(AppConstants.ErrorMessage.SHOW_NOT_FOUND + uuid));

        show.setStatus(Show.ShowStatus.CANCELLED);
        showRepository.save(show);

        log.info("Show cancelled successfully: {}", uuid);
    }
}
