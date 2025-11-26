package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import org.springframework.data.domain.Page;
import bbu.solution.logwatchai.domain.report.DailyReport;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Path;

/**
 * Service interface for managing log entries, ingestion, analysis,
 * and reporting. Provides operations for storing logs, triggering AI-based
 * analysis, generating reports, and retrieving logs with filtering
 * and pagination support.
 */
public interface LogEntryService {

    /**
     * Retrieves a log entry by its unique identifier.
     *
     * @param logEntryId the UUID of the log entry
     * @return an Optional containing the log entry if found
     */
    Optional<LogEntry> getLogEntryById(UUID logEntryId);

    /**
     * Checks whether a log entry already exists for a given source and raw text.
     *
     * @param sourceId the UUID of the log source
     * @param rawText the raw log text
     * @return true if an entry already exists, false otherwise
     */
    boolean doesLogEntryExistsBySourceIdRawText(UUID sourceId, String rawText);

    /**
     * Ingests log entries for the provided log source. Typically used
     * when reading logs from a directory or external system.
     *
     * @param logSource the source from which logs are ingested
     * @return a list of newly created log entries
     */
    List<LogEntry> ingestLog(LogSource logSource);

    /**
     * Saves a single raw log entry for a specific source.
     *
     * @param rawText the raw log content
     * @param sourceId the UUID of the log source
     * @return the persisted LogEntry
     */
    LogEntry saveRawLog(String rawText, UUID sourceId);

    /**
     * Triggers asynchronous AI analysis for the given log entry.
     *
     * @param logEntry the log entry to analyze
     */
    void analyzeAsync(LogEntry logEntry);

    /**
     * Processes all pending, unanalyzed log entries and triggers
     * AI-based analysis for each.
     */
    void analyzePendingLogs();

    /**
     * Marks the log entry with the given ID as analyzed.
     *
     * @param id the UUID of the log entry
     */
    void markAsAnalyzed(UUID id);

    /**
     * Handles file updates from a file-based log ingestion mechanism,
     * called by a filesystem watcher or polling worker.
     *
     * @param source the log source associated with the file
     * @param filePath the updated file path
     */
    void ingestFileUpdate(LogSource source, Path filePath);

    /**
     * Generates a daily report for a specific date.
     *
     * @param date the date for which to generate the report
     * @return the generated DailyReport
     */
    DailyReport generateReport(LocalDate date);

    /**
     * Generates a report over a date range.
     *
     * @param from start date (inclusive)
     * @param to end date (inclusive)
     * @return the generated DailyReport
     */
    DailyReport generateReport(LocalDate from, LocalDate to);

    /**
     * Retrieves log entries by applying the given filter criteria.
     *
     * @param filter the filter object
     * @return a list of matching log entries
     */
    List<LogEntry> getLogs(LogFilter filter);

    /**
     * Retrieves log entries with filtering and pagination support.
     *
     * @param filter the filter criteria
     * @param pageable pagination parameters
     * @return a paginated list of matching log entries
     */
    Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable);
}
