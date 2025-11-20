package bbu.solution.logwatchai.domain.alert;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertService {
    Alert create(Alert alert);
    void deactivate(UUID id);
    Optional<Alert> getAlertById(UUID alertId);
    List<Alert> getActiveAlerts();
    List<Alert> getAlerts(AlertFilter filter);
    Page<Alert> getAlertsPageable(AlertFilter filter, Pageable pageable);

}