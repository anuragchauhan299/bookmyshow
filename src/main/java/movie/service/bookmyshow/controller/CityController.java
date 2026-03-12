package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.entity.City;
import movie.service.bookmyshow.service.LocalizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
