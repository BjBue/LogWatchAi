package bbu.solution.logwatchai.domain.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing {@link DailyReport} entities.
 * <p>
 * Implementations of this service are responsible for generating, retrieving,
 * and updating daily reports. A daily report summarizes log activity,
 * anomalies, and analytics for a specific calendar date.
 */
public interface DailyReportService {

    /**
     * Generates a new daily report for the specified date.
     * <p>
     * If a report already exists for that date, the implementation may either
     * overwrite it or throw an exception, depending on the chosen design.
     *
     * @param date the date for which the report should be generated
     * @return the newly generated {@link DailyReport}
     */
    DailyReport generateForDate(LocalDate date);

    /**
     * Marks the report with the given ID as delivered.
     *
     * @param reportId the unique identifier of the report to update
     */
    void markDelivered(UUID reportId);

    /**
     * Retrieves the daily report for a specific date.
     *
     * @param date the date of the requested report
     * @return an {@link Optional} containing the report if found,
     *         or empty if no report exists for that date
     */
    Optional<DailyReport> getByDate(LocalDate date);

    /**
     * Returns the report for the given date, generating it if it does not exist.
     *
     * @param date the date for which a report is required
     * @return an existing or newly created {@link DailyReport}
     */
    DailyReport getOrCreateReport(LocalDate date);

    /**
     * Retrieves all daily reports stored in the system.
     *
     * @return a list of all {@link DailyReport} instances
     */
    List<DailyReport> getAll();
}
