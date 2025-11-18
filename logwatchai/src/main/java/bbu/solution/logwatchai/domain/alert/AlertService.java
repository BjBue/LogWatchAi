package bbu.solution.logwatchai.domain.alert;

import java.util.List;
import java.util.UUID;

public interface AlertService {
    Alert create(Alert alert);
    void deactivate(UUID id);
    List<Alert> getActiveAlerts();
}