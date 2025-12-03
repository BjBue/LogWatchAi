package bbu.solution.logwatchai.infrastructure.persistence.report;

import bbu.solution.logwatchai.domain.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing DailyReport entities in the database.
 * Provides standard CRUD operations and a query to fetch a report by its reported date.
 */
public interface DailyReportRepository extends JpaRepository<DailyReport, UUID> {

    /**
     * Finds a DailyReport by its reported date.
     *
     * @param date the date of the report
     * @return an Optional containing the DailyReport if found, empty otherwise
     */
    Optional<DailyReport> findByReportedDate(LocalDate date);
}
