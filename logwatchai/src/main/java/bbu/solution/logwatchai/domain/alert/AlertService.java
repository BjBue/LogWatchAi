package bbu.solution.logwatchai.domain.alert;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing {@link Alert} entities.
 *
 * <p>This service defines the domain-level operations for creating, retrieving,
 * filtering, and updating alert states. Implementations are responsible for
 * applying business rules, validation, and interacting with the persistence layer.</p>
 */
public interface AlertService {

    /**
     * Creates and persists a new alert.
     *
     * @param alert the {@link Alert} instance to create
     * @return the persisted alert, including its generated identifier
     */
    Alert create(Alert alert);

    /**
     * Marks an alert as inactive (soft deactivation).
     *
     * <p>If the alert exists, its {@code active} flag will be set to {@code false}.
     * If the alert does not exist, the implementation may ignore the call or
     * throw an exception, depending on business rules.</p>
     *
     * @param id the unique identifier of the alert to deactivate
     */
    void deactivate(UUID id);

    /**
     * Retrieves an alert by its unique identifier.
     *
     * @param alertId the alert ID
     * @return an {@link Optional} containing the alert if found; otherwise empty
     */
    Optional<Alert> getAlertById(UUID alertId);

    /**
     * Retrieves all alerts that are currently active.
     *
     * @return a list of active alerts
     */
    List<Alert> getActiveAlerts();

    /**
     * Retrieves alerts filtered by the provided {@link AlertFilter} criteria.
     *
     * <p>Only non-null filter values should be applied. Implementations typically
     * create dynamic queries based on the filter attributes.</p>
     *
     * @param filter the filtering criteria
     * @return a list of matching alerts
     */
    List<Alert> getAlerts(AlertFilter filter);

    /**
     * Retrieves alerts matching the provided filter, returned as a pageable result.
     *
     * <p>This method supports pagination, sorting, and dynamic filtering.
     * It is typically used for UI tables or REST endpoints with paging support.</p>
     *
     * @param filter   filtering criteria
     * @param pageable pagination and sorting information
     * @return a {@link Page} of alert results
     */
    Page<Alert> getAlertsPageable(AlertFilter filter, Pageable pageable);
}
