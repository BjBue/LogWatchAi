package bbu.solution.logwatchai.application.alert;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.infrastructure.persistence.alert.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository repository;

    @Override
    public Alert create(Alert alert) {
        return repository.save(alert);
    }

    @Override
    public void deactivate(UUID id) {
        repository.findById(id).ifPresent(alert -> {
            alert.deactivate();
            repository.save(alert);
        });
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return repository.findByActiveTrueOrderByCreatedAtDesc();
    }
}
