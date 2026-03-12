package movie.service.bookmyshow.controller;

import movie.service.bookmyshow.dto.ShowDto;
import movie.service.bookmyshow.entity.Show;
import movie.service.bookmyshow.service.ShowService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public List<Show> getShows(@RequestParam(required = false) String movieTitle,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) LocalDate date) {
        return showService.findShows(movieTitle, city, date);
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody ShowDto dto) {
        Show show = showService.createShow(mapToShow(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(show);
    }

    @PutMapping("/{id}")
    public Show updateShow(@PathVariable String id, @RequestBody ShowDto dto) {
        return showService.updateShow(id, mapToShow(dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShow(@PathVariable String id) {
        showService.deleteShow(id);
    }

    private Show mapToShow(ShowDto dto) {
        return Show.builder()
                .showDate(dto.getShowDate())
                .showTime(dto.getShowTime())
                .ticketPrice(dto.getTicketPrice())
                .build();
    }
}
