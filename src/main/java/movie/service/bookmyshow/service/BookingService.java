package movie.service.bookmyshow.service;

import movie.service.bookmyshow.constant.AppConstants;
import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.*;
import movie.service.bookmyshow.exception.BookingException;
import movie.service.bookmyshow.exception.SeatNotAvailableException;
import movie.service.bookmyshow.exception.ShowNotFoundException;
import movie.service.bookmyshow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final OfferRepository offerRepository;
    private final BookMyShowProperties properties;

    @Transactional
    public Booking createBooking(String showUuid, List<String> seatNumbers, String userId, String userEmail, String userPhone) {
        log.info("Creating booking for show {} by user {}", showUuid, userId);

        Show show = showRepository.findByUuidWithLock(showUuid)
                .orElseThrow(() -> new ShowNotFoundException(AppConstants.ErrorMessage.SHOW_NOT_FOUND + showUuid));

        if (show.getStatus() != Show.ShowStatus.ACTIVE) {
            throw new BookingException(AppConstants.ErrorMessage.SHOW_NOT_ACTIVE);
        }

        validateSeatLimit(seatNumbers);
        validateUserBookingLimit(userId);

        List<Seat> seats = seatRepository.findByShowUuidAndSeatNumbers(showUuid, new HashSet<>(seatNumbers));

        if (seats.size() != seatNumbers.size()) {
            throw new SeatNotAvailableException(AppConstants.ErrorMessage.SEAT_DOES_NOT_EXIST);
        }

        for (Seat seat : seats) {
            if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException(String.format(AppConstants.ErrorMessage.SEAT_NOT_AVAILABLE, seat.getSeatNumber()));
            }
            if (seat.getHoldExpiry() != null && seat.getHoldExpiry().isAfter(LocalDateTime.now())) {
                throw new SeatNotAvailableException(String.format(AppConstants.ErrorMessage.SEAT_CURRENTLY_HELD, seat.getSeatNumber()));
            }
        }

        PricingDetails pricing = calculatePricing(show, seats);

        Booking booking = Booking.builder()
                .show(show)
                .userId(userId)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .seats(seats)
                .basePrice(pricing.basePrice())
                .discountAmount(pricing.discountAmount())
                .platformCommission(pricing.platformCommission())
                .gstAmount(pricing.gstAmount())
                .totalPrice(pricing.totalPrice())
                .appliedOffers(pricing.appliedOffers())
                .status(Booking.BookingStatus.PENDING)
                .paymentStatus(Booking.PaymentStatus.PENDING)
                .build();

        for (Seat seat : seats) {
            seat.setStatus(Seat.SeatStatus.BOOKED);
            seat.setBooking(booking);
        }

        seatRepository.saveAll(seats);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking created successfully: {}", savedBooking.getBookingReference());
        return savedBooking;
    }

    @Transactional
    public Booking confirmBooking(String bookingUuid, String paymentId, String paymentGateway) {
        log.info("Confirming booking {} with payment {}", bookingUuid, paymentId);

        Booking booking = bookingRepository.findByUuid(bookingUuid)
                .orElseThrow(() -> new BookingException(AppConstants.ErrorMessage.BOOKING_NOT_FOUND + bookingUuid));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BookingException(AppConstants.ErrorMessage.BOOKING_NOT_PENDING);
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.CAPTURED);
        booking.setPaymentId(paymentId);
        booking.setPaymentGateway(paymentGateway);
        booking.setUpdatedAt(LocalDateTime.now());

        log.info("Booking confirmed: {}", booking.getBookingReference());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(String bookingUuid, String reason) {
        log.info("Cancelling booking: {}", bookingUuid);

        Booking booking = bookingRepository.findByUuid(bookingUuid)
                .orElseThrow(() -> new BookingException(AppConstants.ErrorMessage.BOOKING_NOT_FOUND + bookingUuid));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BookingException(AppConstants.ErrorMessage.BOOKING_ALREADY_CANCELLED);
        }

        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED && booking.getPaymentStatus() == Booking.PaymentStatus.CAPTURED) {
            BigDecimal refundAmount = calculateRefund(booking);
            booking.setRefundAmount(refundAmount);
            booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        }

        for (Seat seat : booking.getSeats()) {
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
            seat.setBooking(null);
            seat.setHoldExpiry(null);
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(reason);
        booking.setUpdatedAt(LocalDateTime.now());

        seatRepository.saveAll(booking.getSeats());
        log.info("Booking cancelled: {}", bookingUuid);
        return bookingRepository.save(booking);
    }

    public List<Seat> getAvailableSeats(String showUuid) {
        return seatRepository.findByShowUuidAndStatus(showUuid, Seat.SeatStatus.AVAILABLE);
    }

    public Booking getBooking(String uuid) {
        return bookingRepository.findByUuid(uuid)
                .orElseThrow(() -> new BookingException(AppConstants.ErrorMessage.BOOKING_NOT_FOUND + uuid));
    }

    private void validateSeatLimit(List<String> seatNumbers) {
        int maxSeats = properties.getBooking().getMaxSeatsPerBooking();
        if (seatNumbers.size() > maxSeats) {
            throw new BookingException(String.format(AppConstants.ErrorMessage.MAX_SEATS_EXCEEDED, maxSeats));
        }
    }

    private void validateUserBookingLimit(String userId) {
        int maxBookings = properties.getBooking().getMaxBookingsPerUserPerDay();
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int todayBookings = bookingRepository.countConfirmedBookingsByUserToday(userId, startOfDay);
        if (todayBookings >= maxBookings) {
            throw new BookingException(String.format(AppConstants.ErrorMessage.MAX_BOOKINGS_EXCEEDED, maxBookings));
        }
    }

    private PricingDetails calculatePricing(Show show, List<Seat> seats) {
        BigDecimal basePrice = show.getTicketPrice();
        BigDecimal totalBase = basePrice.multiply(BigDecimal.valueOf(seats.size()));

        BigDecimal discountAmount = BigDecimal.ZERO;
        List<String> appliedOffers = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        String city = show.getCity();
        String theatreName = show.getTheatre().getName();

        List<Offer> applicableOffers = offerRepository.findApplicableOffers(now, city, theatreName);

        for (Offer offer : applicableOffers) {
            if (offer.getType() == Offer.OfferType.THIRD_TICKET_DISCOUNT && seats.size() >= 3) {
                int freeTicketCount = seats.size() / 3;
                BigDecimal offerDiscount = basePrice.multiply(BigDecimal.valueOf(freeTicketCount))
                        .multiply(BigDecimal.valueOf(properties.getOffers().getThirdTicketDiscountRate()));
                discountAmount = discountAmount.add(offerDiscount);
                appliedOffers.add(AppConstants.Offer.THIRD_TICKET_DISCOUNT_APPLIED);
            }

            if (offer.getType() == Offer.OfferType.AFTERNOON_SHOW && isAfternoonShow(show.getShowTime())) {
                BigDecimal afternoonDiscount = totalBase.multiply(BigDecimal.valueOf(properties.getOffers().getAfternoonShowDiscountRate()));
                discountAmount = discountAmount.add(afternoonDiscount);
                appliedOffers.add(AppConstants.Offer.AFTERNOON_SHOW_DISCOUNT_APPLIED);
            }
        }

        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        BigDecimal subtotal = totalBase.subtract(discountAmount);

        BigDecimal platformCommission = subtotal.multiply(
                BigDecimal.valueOf(properties.getMonetization().getPlatformCommissionPercent() / 100.0)
        );

        BigDecimal gstAmount = subtotal.multiply(
                BigDecimal.valueOf(properties.getMonetization().getGstRate() / 100.0)
        );

        BigDecimal totalPrice = subtotal.add(gstAmount);

        return new PricingDetails(totalBase, discountAmount, platformCommission, gstAmount, totalPrice, appliedOffers);
    }

    private boolean isAfternoonShow(java.time.LocalTime time) {
        if (time == null) return false;
        String startStr = properties.getOffers().getAfternoonStart();
        String endStr = properties.getOffers().getAfternoonEnd();
        java.time.LocalTime start = java.time.LocalTime.parse(startStr);
        java.time.LocalTime end = java.time.LocalTime.parse(endStr);
        return !time.isBefore(start) && !time.isAfter(end);
    }

    private BigDecimal calculateRefund(Booking booking) {
        return booking.getTotalPrice();
    }

    private record PricingDetails(
            BigDecimal basePrice,
            BigDecimal discountAmount,
            BigDecimal platformCommission,
            BigDecimal gstAmount,
            BigDecimal totalPrice,
            List<String> appliedOffers
    ) {}
}
