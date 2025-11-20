package bbu.solution.logwatchai.application.alert;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.alert.AlertFilter;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.infrastructure.persistence.alert.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Override
    public Alert create(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public void deactivate(UUID id) {
        alertRepository.findById(id).ifPresent(alert -> {
            alert.deactivate();
            alertRepository.save(alert);
        });
    }

    @Override
    public Optional<Alert> getAlertById(UUID id){
        return alertRepository.findById(id);
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    public List<Alert> getAlerts(AlertFilter filter) {
        return alertRepository.findAll();
    }

    @Override
    public Page<Alert> getAlertsPageable(AlertFilter filter, Pageable pageable){
        return alertRepository.findAll(pageable);
    }
}
