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

    @Query("SELECT m FROM Movie m WHERE m.status = 'NOW_SHOWING' AND :city MEMBER OF m.availableCities")
    List<Movie> findNowShowingInCity(@Param("city") String city);
}
