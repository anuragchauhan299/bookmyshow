package movie.service.bookmyshow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import movie.service.bookmyshow.dto.OfferDto;
import movie.service.bookmyshow.entity.Offer;
import movie.service.bookmyshow.exception.OfferNotFoundException;
import movie.service.bookmyshow.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferService {

    private final OfferRepository offerRepository;

    @Transactional
    public Offer createOffer(OfferDto dto) {
        log.info("Creating offer: {}", dto.getCode());

        Offer offer = Offer.builder()
                .uuid(UUID.randomUUID().toString())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .type(Offer.OfferType.valueOf(dto.getType()))
                .discountType(Offer.DiscountType.valueOf(dto.getDiscountType()))
                .discountValue(dto.getDiscountValue())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .minBookingAmount(dto.getMinBookingAmount())
                .minTicketsRequired(dto.getMinTicketsRequired())
                .maxUsageTotal(dto.getMaxUsageTotal())
                .maxUsagePerUser(dto.getMaxUsagePerUser())
                .applicableCities(dto.getApplicableCities())
                .applicableTheatres(dto.getApplicableTheatres())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .priority(dto.getPriority() != null ? dto.getPriority() : 0)
                .build();

        return offerRepository.save(offer);
    }

    @Transactional
    public Offer updateOffer(String uuid, OfferDto dto) {
        log.info("Updating offer: {}", uuid);

        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found: " + uuid));

        offer.setCode(dto.getCode());
        offer.setName(dto.getName());
        offer.setDescription(dto.getDescription());
        offer.setType(Offer.OfferType.valueOf(dto.getType()));
        offer.setDiscountType(Offer.DiscountType.valueOf(dto.getDiscountType()));
        offer.setDiscountValue(dto.getDiscountValue());
        offer.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        offer.setMinBookingAmount(dto.getMinBookingAmount());
        offer.setMinTicketsRequired(dto.getMinTicketsRequired());
        offer.setMaxUsageTotal(dto.getMaxUsageTotal());
        offer.setMaxUsagePerUser(dto.getMaxUsagePerUser());
        offer.setApplicableCities(dto.getApplicableCities());
        offer.setApplicableTheatres(dto.getApplicableTheatres());
        offer.setStartDate(dto.getStartDate());
        offer.setEndDate(dto.getEndDate());
        offer.setIsActive(dto.getIsActive());
        offer.setPriority(dto.getPriority());

        return offerRepository.save(offer);
    }

    @Transactional
    public void deleteOffer(String uuid) {
        log.info("Deleting offer: {}", uuid);

        Offer offer = offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found: " + uuid));

        offer.setIsActive(false);
        offerRepository.save(offer);
    }

    public Offer getOffer(String uuid) {
        return offerRepository.findByUuid(uuid)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found: " + uuid));
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public List<Offer> getActiveOffers() {
        return offerRepository.findByIsActiveTrue();
    }
}
