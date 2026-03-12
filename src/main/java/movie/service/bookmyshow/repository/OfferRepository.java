package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByUuid(String uuid);

    List<Offer> findByIsActiveTrue();

    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND o.startDate <= :now AND o.endDate >= :now AND " +
            "(:city IS NULL OR :city MEMBER OF o.applicableCities) AND " +
            "(:theatre IS NULL OR :theatre MEMBER OF o.applicableTheatres)")
    List<Offer> findApplicableOffers(@Param("now") LocalDateTime now, @Param("city") String city, @Param("theatre") String theatre);
}
