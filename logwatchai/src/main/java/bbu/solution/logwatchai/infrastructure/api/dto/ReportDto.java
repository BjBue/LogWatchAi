package bbu.solution.logwatchai.infrastructure.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing a full aggregated report containing
 * logs, alerts, analyses, and statistical information for a given period.
 * <p>
 * This DTO is optimized for API responses and avoids exposing internal domain
 * models directly. It groups related summary and detail sections in nested
 * record types for clarity and structured serialization.
 *
 * @param period     the covered time range of the report
 * @param summary    statistical summary of logs, alerts, and analysis data
 * @param logs       a list of log entry items included in the report
 * @param alerts     a list of alert items generated during the period
 * @param analysis   a list of AI analysis items referenced in the report
 * @param topIssues  aggregated top issues or patterns identified, if available
 */
public record ReportDto(
        Period period,
        Summary summary,
        List<LogItem> logs,
        List<AlertItem> alerts,
        List<AnalysisItem> analysis,
        List<Map<String, Object>> topIssues
) {

    /**
     * Represents the time range covered by the generated report.
     *
     * @param from the start timestamp of the reporting period
     * @param to   the end timestamp of the reporting period
     */
    public record Period(Instant from, Instant to) {}

    /**
     * Aggregated summary metrics for the reporting period.
     *
     * @param totalLogs       total number of logs detected
     * @param totalAlerts     total number of alerts generated
     * @param totalAnalysis   total number of AI analyses performed
     * @param logsPerSource   a map of source ID to log count for that source
     */
    public record Summary(long totalLogs, long totalAlerts, long totalAnalysis, Map<String, Long> logsPerSource) {}

    /**
     * DTO representation of an individual log record used in reports.
     *
     * @param id            log entry identifier
     * @param ingestionTime timestamp when the log entered the system
     * @param level         extracted log level, if present
     * @param rawText       complete raw log message
     * @param sourceId      ID of the originating log source
     */
    public record LogItem(String id, Instant ingestionTime, String level, String rawText, String sourceId) {}

    /**
     * DTO representing an alert generated during the report period.
     *
     * @param id          alert identifier
     * @param createdAt   timestamp of alert creation
     * @param severity    severity level of the alert
     * @param message     alert message content
     * @param sourceId    log source associated with the alert
     * @param logEntryId  referenced log entry ID, if applicable
     */
    public record AlertItem(String id, Instant createdAt, String severity, String message, String sourceId, String logEntryId) {}

    /**
     * DTO representing an AI analysis item included in the report.
     *
     * @param id               analysis identifier
     * @param analyzedAt       timestamp when analysis was completed
     * @param severity         severity associated with the analysis
     * @param anomalyScore     anomaly probability score (0.0–1.0)
     * @param summarizedIssue  short textual summary of the issue
     * @param logEntryId       referenced log entry ID
     */
    public record AnalysisItem(String id, Instant analyzedAt, String severity, double anomalyScore, String summarizedIssue, String logEntryId) {}
}
