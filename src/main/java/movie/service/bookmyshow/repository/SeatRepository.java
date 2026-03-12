package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowUuidAndStatus(String showUuid, Seat.SeatStatus status);

    @Query("SELECT s FROM Seat s WHERE s.show.uuid = :showUuid AND s.seatNumber IN :seatNumbers")
    List<Seat> findByShowUuidAndSeatNumbers(@Param("showUuid") String showUuid, @Param("seatNumbers") Set<String> seatNumbers);
}
