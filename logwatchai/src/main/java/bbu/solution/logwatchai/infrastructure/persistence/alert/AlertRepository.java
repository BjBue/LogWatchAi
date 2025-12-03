package bbu.solution.logwatchai.infrastructure.persistence.alert;

import bbu.solution.logwatchai.domain.alert.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Alert entities in the database.
 * Extends JpaRepository to provide standard CRUD operations.
 *
 * Includes additional methods for retrieving active alerts and alerts for reports.
 */
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    /**
     * Retrieves all active alerts, sorted by creation timestamp in descending order.
     *
     * @return a list of active alerts sorted from newest to oldest
     */
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Retrieves all alerts created after the specified timestamp, sorted by creation timestamp in ascending order.
     * This is typically used for generating reports.
     *
     * @param since the lower bound timestamp (exclusive) for alert creation
     * @return a list of alerts created after the given timestamp, sorted oldest to newest
     */
    List<Alert> findByCreatedAtAfterOrderByCreatedAtAsc(Instant since);
}
