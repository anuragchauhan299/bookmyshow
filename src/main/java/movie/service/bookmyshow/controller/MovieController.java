package movie.service.bookmyshow.controller;

import movie.service.bookmyshow.entity.Movie;
import movie.service.bookmyshow.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final LocalizationService localizationService;

    @GetMapping
    public List<Movie> getMovies(@RequestParam(required = false) String city,
                                  @RequestParam(required = false, defaultValue = "en-US") String locale) {
        if (city != null) {
            return localizationService.getMoviesNowShowingInCity(city, locale);
        }
        return List.of();
    }
}
