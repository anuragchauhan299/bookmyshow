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

    public City getCityByNameAndCountry(String name, String country) {
        return cityRepository.findByNameAndCountry(name, country)
                .orElse(null);
    }

    public String getCityTimezone(String cityName, String country) {
        City city = getCityByNameAndCountry(cityName, country);
        if (city != null) {
            return city.getTimezone();
        }
        return ZoneId.systemDefault().getId();
    }

    public String getCityCurrency(String cityName, String country) {
        City city = getCityByNameAndCountry(cityName, country);
        if (city != null) {
            return city.getCurrencyCode();
        }
        return "INR";
    }

    public String getCityLocale(String cityName, String country) {
        City city = getCityByNameAndCountry(cityName, country);
        if (city != null) {
            return city.getLocale();
        }
        return properties.getLocalization().getDefaultLocale();
    }

    public Set<String> getSupportedLocales() {
        return Set.of(properties.getLocalization().getSupportedLocales().toArray(new String[0]));
    }

    public boolean isLocaleSupported(String locale) {
        return getSupportedLocales().contains(locale);
    }

    public Locale resolveLocale(String requestedLocale) {
        if (requestedLocale == null || requestedLocale.isEmpty()) {
            return Locale.forLanguageTag(properties.getLocalization().getDefaultLocale());
        }
        
        if (isLocaleSupported(requestedLocale)) {
            return Locale.forLanguageTag(requestedLocale);
        }
        
        String language = requestedLocale.split("-")[0];
        for (String supported : properties.getLocalization().getSupportedLocales()) {
            if (supported.startsWith(language)) {
                return Locale.forLanguageTag(supported);
            }
        }
        
        return Locale.forLanguageTag(properties.getLocalization().getDefaultLocale());
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
