package movie.service.bookmyshow.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import movie.service.bookmyshow.entity.*;
import movie.service.bookmyshow.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final OfferRepository offerRepository;

    @Override
    public void run(String... args) {
        if (cityRepository.count() == 0) {
            loadCities();
        }
        if (movieRepository.count() == 0) {
            loadMovies();
        }
        if (theatreRepository.count() == 0) {
            loadTheatres();
        }
        if (showRepository.count() == 0) {
            loadShows();
        }
        if (offerRepository.count() == 0) {
            loadOffers();
        }
        log.info("Data initialization completed");
    }

    private void loadCities() {
        List<City> cities = Arrays.asList(
                City.builder().uuid(UUID.randomUUID().toString()).name("Mumbai").state("Maharashtra")
                        .country("India").countryCode("IN").timezone("Asia/Kolkata")
                        .currencyCode("INR").currencySymbol("₹").locale("en-IN")
                        .defaultLanguage("Hindi").taxRate(java.math.BigDecimal.valueOf(18.0)).active(true).displayOrder(1).build(),
                City.builder().uuid(UUID.randomUUID().toString()).name("Delhi").state("Delhi")
                        .country("India").countryCode("IN").timezone("Asia/Kolkata")
                        .currencyCode("INR").currencySymbol("₹").locale("en-IN")
                        .defaultLanguage("Hindi").taxRate(java.math.BigDecimal.valueOf(18.0)).active(true).displayOrder(2).build(),
                City.builder().uuid(UUID.randomUUID().toString()).name("Bangalore").state("Karnataka")
                        .country("India").countryCode("IN").timezone("Asia/Kolkata")
                        .currencyCode("INR").currencySymbol("₹").locale("kn-IN")
                        .defaultLanguage("Kannada").taxRate(java.math.BigDecimal.valueOf(18.0)).active(true).displayOrder(3).build(),
                City.builder().uuid(UUID.randomUUID().toString()).name("Chennai").state("Tamil Nadu")
                        .country("India").countryCode("IN").timezone("Asia/Kolkata")
                        .currencyCode("INR").currencySymbol("₹").locale("ta-IN")
                        .defaultLanguage("Tamil").taxRate(java.math.BigDecimal.valueOf(18.0)).active(true).displayOrder(4).build(),
                City.builder().uuid(UUID.randomUUID().toString()).name("Hyderabad").state("Telangana")
                        .country("India").countryCode("IN").timezone("Asia/Kolkata")
                        .currencyCode("INR").currencySymbol("₹").locale("te-IN")
                        .defaultLanguage("Telugu").taxRate(java.math.BigDecimal.valueOf(18.0)).active(true).displayOrder(5).build()
        );
        cityRepository.saveAll(cities);
        log.info("Loaded {} cities", cities.size());
    }

    private void loadMovies() {
        List<Movie> movies = Arrays.asList(
                Movie.builder().uuid(UUID.randomUUID().toString()).title("Inception")
                        .description("A thief who steals corporate secrets through dream-sharing technology")
                        .releaseDate(LocalDate.of(2010, 7, 16)).durationMinutes(148)
                        .genre("Sci-Fi").language("English").country("USA")
                        .director("Christopher Nolan").status(Movie.MovieStatus.NOW_SHOWING)
                        .localizedTitles(Map.of("en-US", "Inception", "hi-IN", "इनसेप्शन", "ta-IN", "இன்செப்சன்"))
                        .localizedDescriptions(Map.of("en-US", "A thief who steals corporate secrets...",
                                "hi-IN", "एक चोर जो ड्रीम-शेयरिंग तकनीक के माध्यम से कॉर्पोरेट रहस्य चुराता है"))
                        .availableCities(new HashSet<>(Arrays.asList("Mumbai", "Delhi", "Bangalore", "Chennai", "Hyderabad")))
                        .build(),
                Movie.builder().uuid(UUID.randomUUID().toString()).title("Dunkirk")
                        .description("Allied soldiers are surrounded by the German Army")
                        .releaseDate(LocalDate.of(2017, 7, 21)).durationMinutes(106)
                        .genre("War").language("English").country("USA")
                        .director("Christopher Nolan").status(Movie.MovieStatus.NOW_SHOWING)
                        .localizedTitles(Map.of("en-US", "Dunkirk", "hi-IN", "डंकर्क"))
                        .availableCities(new HashSet<>(Arrays.asList("Mumbai", "Delhi", "Bangalore")))
                        .build(),
                Movie.builder().uuid(UUID.randomUUID().toString()).title("Oppenheimer")
                        .description("The story of American scientist J. Robert Oppenheimer")
                        .releaseDate(LocalDate.of(2023, 7, 21)).durationMinutes(180)
                        .genre("Drama").language("English").country("USA")
                        .director("Christopher Nolan").status(Movie.MovieStatus.NOW_SHOWING)
                        .localizedTitles(Map.of("en-US", "Oppenheimer", "hi-IN", "ओपेनहाइमर"))
                        .availableCities(new HashSet<>(Arrays.asList("Mumbai", "Delhi", "Bangalore", "Chennai", "Hyderabad")))
                        .build()
        );
        movieRepository.saveAll(movies);
        log.info("Loaded {} movies", movies.size());
    }

    private void loadTheatres() {
        List<Theatre> theatres = Arrays.asList(
                Theatre.builder().uuid(UUID.randomUUID().toString()).name("PVR Andheri")
                        .address("Infinity Mall, Andheri West").city("Mumbai").state("Maharashtra")
                        .country("India").type(Theatre.TheatreType.MULTIPLEX)
                        .integrationType(Theatre.IntegrationType.NEW)
                        .screenCount(4).totalSeats(400).active(true).build(),
                Theatre.builder().uuid(UUID.randomUUID().toString()).name("INOX Nariman Point")
                        .address("Nariman Point").city("Mumbai").state("Maharashtra")
                        .country("India").type(Theatre.TheatreType.MULTIPLEX)
                        .integrationType(Theatre.IntegrationType.LEGACY_REST)
                        .integrationEndpoint("https://legacy-theatre-api.example.com/theatre1")
                        .screenCount(3).totalSeats(300).active(true).build(),
                Theatre.builder().uuid(UUID.randomUUID().toString()).name("PVR Koramangala")
                        .address("Koramangala").city("Bangalore").state("Karnataka")
                        .country("India").type(Theatre.TheatreType.MULTIPLEX)
                        .integrationType(Theatre.IntegrationType.NEW)
                        .screenCount(5).totalSeats(500).active(true).build(),
                Theatre.builder().uuid(UUID.randomUUID().toString()).name("inox delhi")
                        .address("Delhi").city("Delhi").state("Delhi")
                        .country("India").type(Theatre.TheatreType.MULTIPLEX)
                        .integrationType(Theatre.IntegrationType.NEW)
                        .screenCount(4).totalSeats(400).active(true).build()
        );
        theatreRepository.saveAll(theatres);
        log.info("Loaded {} theatres", theatres.size());
    }

    private void loadShows() {
        List<Theatre> theatres = theatreRepository.findAll();
        List<Movie> movies = movieRepository.findAll();

        if (theatres.isEmpty() || movies.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        List<Show> shows = new ArrayList<>();

        shows.add(createShow(theatres.get(0), movies.get(0), today, LocalTime.of(14, 0), BigDecimal.valueOf(300)));
        shows.add(createShow(theatres.get(1), movies.get(0), today, LocalTime.of(19, 30), BigDecimal.valueOf(350)));
        shows.add(createShow(theatres.get(2), movies.get(0), today, LocalTime.of(15, 0), BigDecimal.valueOf(280)));
        shows.add(createShow(theatres.get(0), movies.get(1), today, LocalTime.of(11, 0), BigDecimal.valueOf(250)));
        shows.add(createShow(theatres.get(1), movies.get(2), today, LocalTime.of(21, 0), BigDecimal.valueOf(400)));

        showRepository.saveAll(shows);
        log.info("Loaded {} shows", shows.size());
    }

    private Show createShow(Theatre theatre, Movie movie, LocalDate date, LocalTime time, BigDecimal price) {
        return Show.builder()
                .uuid(UUID.randomUUID().toString())
                .theatre(theatre)
                .movie(movie)
                .showDate(date)
                .showTime(time)
                .ticketPrice(price)
                .city(theatre.getCity())
                .status(Show.ShowStatus.ACTIVE)
                .totalSeats(100)
                .availableSeats(100)
                .build();
    }

    private void loadOffers() {
        List<Offer> offers = Arrays.asList(
                Offer.builder().uuid(UUID.randomUUID().toString()).code("THIRD_TICKET")
                        .name("Third Ticket 50% Off").description("Get 50% off on the third ticket")
                        .type(Offer.OfferType.THIRD_TICKET_DISCOUNT)
                        .discountType(Offer.DiscountType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(50))
                        .minTicketsRequired(3)
                        .applicableCities(new HashSet<>(Arrays.asList("Mumbai", "Delhi", "Bangalore")))
                        .startDate(LocalDateTime.now().minusDays(30))
                        .endDate(LocalDateTime.now().plusDays(180))
                        .isActive(true).priority(1).build(),
                Offer.builder().uuid(UUID.randomUUID().toString()).code("AFTERNOON20")
                        .name("Afternoon Show 20% Off").description("20% off on afternoon shows")
                        .type(Offer.OfferType.AFTERNOON_SHOW)
                        .discountType(Offer.DiscountType.PERCENTAGE)
                        .discountValue(BigDecimal.valueOf(20))
                        .applicableCities(new HashSet<>(Arrays.asList("Mumbai", "Delhi", "Bangalore")))
                        .applicableTheatres(new HashSet<>(Arrays.asList("PVR Andheri", "INOX Nariman Point", "PVR Koramangala")))
                        .startDate(LocalDateTime.now().minusDays(30))
                        .endDate(LocalDateTime.now().plusDays(180))
                        .isActive(true).priority(2).build()
        );
        offerRepository.saveAll(offers);
        log.info("Loaded {} offers", offers.size());
    }
}
