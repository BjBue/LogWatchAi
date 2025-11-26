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

    /**
     * Creates and persists a new alert.
     *
     * @param alert the alert to create
     * @return the created alert
     */
    @Override
    public Alert create(Alert alert) {
        return alertRepository.save(alert);
    }

    /**
     * Deactivates an alert by its ID if it exists.
     *
     * @param id the unique identifier of the alert
     */
    @Override
    public void deactivate(UUID id) {
        alertRepository.findById(id).ifPresent(alert -> {
            alert.deactivate();
            alertRepository.save(alert);
        });
    }

    /**
     * Retrieves an alert by its ID.
     *
     * @param id the unique identifier of the alert
     * @return an optional containing the alert if found
     */
    @Override
    public Optional<Alert> getAlertById(UUID id){
        return alertRepository.findById(id);
    }

    /**
     * Returns all active alerts ordered by creation date descending.
     *
     * @return a list of active alerts
     */
    @Override
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    /**
     * Retrieves all alerts based on a provided filter.
     * (Currently the filter is not yet applied.)
     *
     * @param filter the filter configuration
     * @return a list of alerts
     */
    @Override
    public List<Alert> getAlerts(AlertFilter filter) {
        return alertRepository.findAll();
    }

    /**
     * Retrieves alerts in a pageable format based on a provided filter.
     * (Currently the filter is not yet applied.)
     *
     * @param filter the filter configuration
     * @param pageable the pageable settings
     * @return a pageable list of alerts
     */
    @Override
    public Page<Alert> getAlertsPageable(AlertFilter filter, Pageable pageable){
        return alertRepository.findAll(pageable);
    }
}
