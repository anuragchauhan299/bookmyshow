package movie.service.bookmyshow.controller;

import movie.service.bookmyshow.entity.City;
import movie.service.bookmyshow.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final LocalizationService localizationService;

    @GetMapping
    public List<City> getCities(@RequestParam(required = false) String country) {
        if (country != null) {
            return localizationService.getCitiesByCountry(country);
        }
        return localizationService.getActiveCities();
    }
}
