package movie.service.bookmyshow.service;

import movie.service.bookmyshow.constant.AppConstants;
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
}
