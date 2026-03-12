package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.dto.TheatreDto;
import movie.service.bookmyshow.entity.Theatre;
import movie.service.bookmyshow.service.TheatreIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreIntegrationService theatreIntegrationService;

    @GetMapping
    public List<Theatre> getTheatres(@RequestParam String city) {
        return theatreIntegrationService.getTheatresByCity(city);
    }

    @PostMapping
    public ResponseEntity<Theatre> registerTheatre(@RequestBody TheatreDto dto) {
        Theatre theatre = Theatre.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .name(dto.getName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .type(dto.getType() != null ? dto.getType() : Theatre.TheatreType.MULTIPLEX)
                .integrationType(dto.getIntegrationType() != null ? dto.getIntegrationType() : Theatre.IntegrationType.NEW)
                .integrationEndpoint(dto.getIntegrationEndpoint())
                .screenCount(dto.getScreenCount())
                .totalSeats(dto.getTotalSeats())
                .active(true)
                .build();

        Theatre saved = theatreIntegrationService.registerTheatre(theatre);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
