package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowUuid(String showUuid);

    List<Seat> findByShowUuidAndStatus(String showUuid, Seat.SeatStatus status);

    Optional<Seat> findByUuid(String uuid);

    @Query("SELECT s FROM Seat s WHERE s.show.uuid = :showUuid AND s.seatNumber IN :seatNumbers")
    List<Seat> findByShowUuidAndSeatNumbers(@Param("showUuid") String showUuid, @Param("seatNumbers") Set<String> seatNumbers);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.uuid = :uuid")
    Optional<Seat> findByUuidWithLock(@Param("uuid") String uuid);

    @Modifying
    @Query("UPDATE Seat s SET s.status = :status, s.booking = NULL, s.holdExpiry = NULL WHERE s.holdExpiry < :now AND s.status = 'HELD'")
    int releaseExpiredHolds(@Param("now") LocalDateTime now, @Param("status") Seat.SeatStatus status);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.show.uuid = :showUuid AND s.status = 'AVAILABLE'")
    long countAvailableSeats(@Param("showUuid") String showUuid);

    @Query("SELECT s FROM Seat s WHERE s.show.uuid = :showUuid AND s.status = 'BOOKED'")
    List<Seat> findBookedSeats(@Param("showUuid") String showUuid);

    boolean existsByShowUuidAndSeatNumber(String showUuid, String seatNumber);
}
