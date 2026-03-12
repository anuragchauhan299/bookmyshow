package movie.service.bookmyshow.repository;

import movie.service.bookmyshow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
