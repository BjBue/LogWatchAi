package bbu.solution.logwatchai.infrastructure.persistence.alert;

import bbu.solution.logwatchai.domain.alert.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();
    List<Alert> findBySeverityAndActiveTrue(Alert.Severity severity);
}