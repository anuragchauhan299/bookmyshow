package movie.service.bookmyshow.controller;

import lombok.RequiredArgsConstructor;
import movie.service.bookmyshow.dto.*;
import movie.service.bookmyshow.entity.Booking;
import movie.service.bookmyshow.paymentgateway.PaymentRequest;
import movie.service.bookmyshow.paymentgateway.PaymentResult;
import movie.service.bookmyshow.service.BookingService;
import movie.service.bookmyshow.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Booking> bookTickets(@RequestBody BookingDto dto) {
        Booking booking = bookingService.createBooking(
                dto.getShowId(),
                dto.getSeats(),
                dto.getUserId(),
                dto.getUserEmail(),
                dto.getUserPhone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable String id, @RequestBody PaymentDto dto) {
        PaymentResult result = paymentService.processPayment(
                PaymentRequest.builder()
                        .bookingReference("")
                        .amount(dto.getAmount())
                        .currency("INR")
                        .customerEmail(dto.getEmail())
                        .customerPhone(dto.getPhone())
                        .metadata(Map.of("bookingId", id))
                        .build()
        );

        if (result.isSuccess()) {
            Booking booking = bookingService.confirmBooking(id, result.getPaymentId(), "stripe");
            return ResponseEntity.ok(booking);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String id, @RequestBody CancellationDto dto) {
        Booking booking = bookingService.cancelBooking(id, dto.getReason());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/bulk")
    public List<ResponseEntity<Booking>> bulkBook(@RequestBody BulkBookingDto dto) {
        return dto.getBookings().stream()
                .map(bookingReq -> {
                    try {
                        Booking booking = bookingService.createBooking(
                                bookingReq.getShowId(),
                                bookingReq.getSeats(),
                                bookingReq.getUserId(),
                                bookingReq.getUserEmail(),
                                bookingReq.getUserPhone()
                        );
                        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().<Booking>build();
                    }
                })
                .toList();
    }

    @PostMapping("/cancel-bulk")
    public List<ResponseEntity<Booking>> bulkCancel(@RequestBody BulkCancellationDto dto) {
        return dto.getBookingIds().stream()
                .map(id -> {
                    try {
                        Booking booking = bookingService.cancelBooking(id, dto.getReason());
                        return ResponseEntity.ok(booking);
                    } catch (Exception e) {
                        return ResponseEntity.notFound().<Booking>build();
                    }
                })
                .toList();
    }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable String id) {
        return bookingService.getBooking(id);
    }
}
