package bbu.solution.logwatchai.domain.alert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();
    List<Alert> findBySeverityAndActiveTrue(Alert.Severity severity);
}