package movie.service.bookmyshow.service;

import movie.service.bookmyshow.config.BookMyShowProperties;
import movie.service.bookmyshow.entity.AuditLog;
import movie.service.bookmyshow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final BookMyShowProperties properties;

    @Transactional
    public void logAction(String entityType, String entityId, String action, String userId, 
                          String userEmail, String oldValue, String newValue, String description) {
        if (!properties.getCompliance().isEnableAuditLogging()) {
            return;
        }

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .userId(userId)
                .userEmail(userEmail)
                .oldValue(oldValue)
                .newValue(newValue)
                .description(description)
                .correlationId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} {}", entityType, action, entityId);
    }

    public void logBookingCreated(String bookingId, String userId, String showId) {
        logAction("BOOKING", bookingId, "CREATED", userId, null, null, null, 
                "Booking created for show: " + showId);
    }

    public void logBookingConfirmed(String bookingId, String userId, String paymentId) {
        logAction("BOOKING", bookingId, "CONFIRMED", userId, null, null, null, 
                "Booking confirmed with payment: " + paymentId);
    }

    public void logBookingCancelled(String bookingId, String userId, String reason) {
        logAction("BOOKING", bookingId, "CANCELLED", userId, null, null, null, 
                "Booking cancelled. Reason: " + reason);
    }

    public void logPaymentProcessed(String paymentId, String bookingId, String status) {
        logAction("PAYMENT", paymentId, status, null, null, null, null, 
                "Payment processed for booking: " + bookingId);
    }

    public void logUserLogin(String userId, String email, String ipAddress) {
        logAction("USER", userId, "LOGIN", userId, email, null, null, 
                "User login from IP: " + ipAddress);
    }

    public void logSeatSelection(String showId, String seatNumbers, String userId) {
        logAction("SHOW", showId, "SEAT_SELECTED", userId, null, null, seatNumbers, 
                "Seats selected for show");
    }

    public List<AuditLog> getAuditLogsForEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAuditLogsForUser(String userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getAuditLogsBetween(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}
