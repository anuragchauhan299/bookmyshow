package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movie_title", columnList = "title"),
        @Index(name = "idx_movie_release_date", columnList = "release_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column
    private String genre;

    @Column
    private String language;

    @Column
    private String country;

    @Column(name = "director")
    private String director;

    @Column(name = "movie_cast")
    private String movieCast;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieStatus status;

    @ElementCollection
    @CollectionTable(name = "movie_localized_titles", joinColumns = @JoinColumn(name = "movie_id"))
    @MapKeyColumn(name = "locale")
    @Column(name = "localized_title")
    @Builder.Default
    private Map<String, String> localizedTitles = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "movie_localized_descriptions", joinColumns = @JoinColumn(name = "movie_id"))
    @MapKeyColumn(name = "locale")
    @Column(name = "localized_description", length = 2000)
    @Builder.Default
    private Map<String, String> localizedDescriptions = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "movie_cities", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "city")
    @Builder.Default
    private Set<String> availableCities = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        if (status == null) {
            status = MovieStatus.COMING_SOON;
        }
    }

    public enum MovieStatus {
        COMING_SOON, NOW_SHOWING, ENDED
    }
}
