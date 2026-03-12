package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByUuid(String uuid);

    Optional<City> findByNameAndCountry(String name, String country);

    List<City> findByCountry(String country);

    List<City> findByActive(Boolean active);

    List<City> findByCountryOrderByDisplayOrderAsc(String country);

    boolean existsByNameAndCountry(String name, String country);
}
