package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.entity.Movie;
import movie.service.bookmyshow.service.LocalizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
