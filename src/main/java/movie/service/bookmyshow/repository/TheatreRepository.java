package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {


    List<Theatre> findByCityAndActive(String city, Boolean active);

    boolean existsByNameAndCity(String name, String city);
}
