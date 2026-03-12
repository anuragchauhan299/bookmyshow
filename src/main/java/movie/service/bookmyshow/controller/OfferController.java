package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.dto.OfferDto;
import movie.service.bookmyshow.entity.Offer;
import movie.service.bookmyshow.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public List<Offer> getAllOffers(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        return activeOnly ? offerService.getActiveOffers() : offerService.getAllOffers();
    }

    @GetMapping("/{uuid}")
    public Offer getOffer(@PathVariable String uuid) {
        return offerService.getOffer(uuid);
    }

    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody OfferDto dto) {
        Offer offer = offerService.createOffer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(offer);
    }

    @PutMapping("/{uuid}")
    public Offer updateOffer(@PathVariable String uuid, @RequestBody OfferDto dto) {
        return offerService.updateOffer(uuid, dto);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable String uuid) {
        offerService.deleteOffer(uuid);
    }
}
