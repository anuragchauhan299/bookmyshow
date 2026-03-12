package movie.service.bookmyshow.repository;

import jakarta.persistence.LockModeType;
import movie.service.bookmyshow.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    Optional<Show> findByUuid(String uuid);

    List<Show> findByCityAndMovieTitleAndShowDate(String city, String movieTitle, LocalDate showDate);

    List<Show> findByCityAndShowDate(String city, LocalDate showDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Show s WHERE s.uuid = :uuid")
    Optional<Show> findByUuidWithLock(@Param("uuid") String uuid);

    @Query("SELECT s FROM Show s WHERE s.city = :city AND s.movie.title LIKE %:movieTitle% AND s.showDate = :showDate")
    List<Show> searchShows(@Param("city") String city, @Param("movieTitle") String movieTitle, @Param("showDate") LocalDate showDate);
}
