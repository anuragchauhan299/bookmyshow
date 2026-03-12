package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByUuid(String uuid);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.createdAt >= :startDate ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookingsByUser(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.userId = :userId AND b.createdAt >= :startDate AND b.status = 'CONFIRMED'")
    int countConfirmedBookingsByUserToday(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT b FROM Booking b WHERE b.paymentId = :paymentId")
    Optional<Booking> findByPaymentId(String paymentId);

    boolean existsByShowUuidAndUserIdAndStatus(String showUuid, String userId, Booking.BookingStatus status);
}
