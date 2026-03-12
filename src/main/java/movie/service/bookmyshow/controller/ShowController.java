package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.dto.ShowDto;
import movie.service.bookmyshow.entity.Movie;
import movie.service.bookmyshow.entity.Show;
import movie.service.bookmyshow.entity.Theatre;
import movie.service.bookmyshow.repository.MovieRepository;
import movie.service.bookmyshow.repository.TheatreRepository;
import movie.service.bookmyshow.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;

    @GetMapping
    public List<Show> getShows(@RequestParam(required = false) String movieTitle,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) LocalDate date) {
        return showService.findShows(movieTitle, city, date);
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody ShowDto dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        Theatre theatre = theatreRepository.findById(dto.getTheatreId())
                .orElseThrow(() -> new IllegalArgumentException("Theatre not found"));

        Show show = Show.builder()
                .movie(movie)
                .theatre(theatre)
                .city(theatre.getCity())
                .showDate(dto.getShowDate())
                .showTime(dto.getShowTime())
                .ticketPrice(dto.getTicketPrice())
                .build();

        Show created = showService.createShow(show);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Show updateShow(@PathVariable String id, @RequestBody ShowDto dto) {
        return showService.updateShow(id, mapToShow(dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShow(@PathVariable String id) {
        showService.cancelShow(id);
    }

    private Show mapToShow(ShowDto dto) {
        return Show.builder()
                .showDate(dto.getShowDate())
                .showTime(dto.getShowTime())
                .ticketPrice(dto.getTicketPrice())
                .build();
    }
}
