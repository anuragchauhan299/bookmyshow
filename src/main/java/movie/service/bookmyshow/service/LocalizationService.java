package movie.service.bookmyshow.service;

import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.City;
import movie.service.bookmyshow.entity.Movie;
import movie.service.bookmyshow.repository.CityRepository;
import movie.service.bookmyshow.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalizationService {

    private final CityRepository cityRepository;
    private final MovieRepository movieRepository;
    private final BookMyShowProperties properties;

    public String getLocalizedMovieTitle(Movie movie, String locale) {
        if (movie == null) return null;
        
        String localizedTitle = movie.getLocalizedTitles().get(locale);
        if (localizedTitle != null && !localizedTitle.isEmpty()) {
            return localizedTitle;
        }
        
        String defaultLocale = properties.getLocalization().getDefaultLocale();
        localizedTitle = movie.getLocalizedTitles().get(defaultLocale);
        if (localizedTitle != null && !localizedTitle.isEmpty()) {
            return localizedTitle;
        }
        
        return movie.getTitle();
    }

    public String getLocalizedMovieDescription(Movie movie, String locale) {
        if (movie == null) return null;
        
        String localizedDesc = movie.getLocalizedDescriptions().get(locale);
        if (localizedDesc != null && !localizedDesc.isEmpty()) {
            return localizedDesc;
        }
        
        String defaultLocale = properties.getLocalization().getDefaultLocale();
        localizedDesc = movie.getLocalizedDescriptions().get(defaultLocale);
        if (localizedDesc != null && !localizedDesc.isEmpty()) {
            return localizedDesc;
        }
        
        return movie.getDescription();
    }

    public List<City> getCitiesByCountry(String country) {
        return cityRepository.findByCountryOrderByDisplayOrderAsc(country);
    }

    public List<City> getActiveCities() {
        return cityRepository.findByActive(true);
    }



    public List<Movie> getMoviesNowShowingInCity(String city, String locale) {
        List<Movie> movies = movieRepository.findNowShowingInCity(city);
        
        movies.forEach(movie -> {
            movie.setTitle(getLocalizedMovieTitle(movie, locale));
            movie.setDescription(getLocalizedMovieDescription(movie, locale));
        });
        
        return movies;
    }
}
