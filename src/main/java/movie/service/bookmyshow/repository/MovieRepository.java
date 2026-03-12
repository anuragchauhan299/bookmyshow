package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByUuid(String uuid);

    Optional<Movie> findByTitle(String title);

    List<Movie> findByStatus(Movie.MovieStatus status);

    List<Movie> findByLanguage(String language);

    List<Movie> findByCountry(String country);

    @Query("SELECT m FROM Movie m WHERE m.status = 'NOW_SHOWING' AND :city MEMBER OF m.availableCities")
    List<Movie> findNowShowingInCity(@Param("city") String city);

    @Query("SELECT m FROM Movie m WHERE m.status = 'COMING_SON' AND m.releaseDate > CURRENT_DATE ORDER BY m.releaseDate")
    List<Movie> findUpcoming();

    @Query("SELECT DISTINCT m.language FROM Movie m")
    List<String> findAllLanguages();
}
