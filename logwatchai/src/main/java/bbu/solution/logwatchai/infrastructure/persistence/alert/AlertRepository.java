package bbu.solution.logwatchai.infrastructure.persistence.alert;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.analysis.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();
    List<Alert> findBySeverityAndActiveTrue(Severity severity);

    //for reports
    List<Alert> findByCreatedAtAfterOrderByCreatedAtAsc(Instant since);
    List<Alert> findByCreatedAtBetweenOrderByCreatedAtAsc(Instant from, Instant to);
}