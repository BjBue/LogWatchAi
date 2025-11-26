package bbu.solution.logwatchai.infrastructure.api.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a {@code LogEntry} for API exposure.
 * <p>
 * This DTO provides a lightweight, serialization-safe view of log entry data
 * without exposing JPA entities or domain logic to the API layer.
 *
 * @param id            unique identifier of the log entry
 * @param timestamp     timestamp when the log entry was originally created by the source
 * @param ingestionTime timestamp when the log entry was ingested into the system
 * @param level         log level extracted from the raw text (if available)
 * @param analyzed      whether the log entry has already been processed by the analysis engine
 * @param hasAnomaly    indicates if anomaly detection flagged this entry
 * @param sourceId      ID of the log source associated with this entry
 * @param rawText       complete raw log text as ingested
 */
public record LogEntryDto(
        UUID id,
        Instant timestamp,
        Instant ingestionTime,
        String level,
        boolean analyzed,
        boolean hasAnomaly,
        UUID sourceId,
        String rawText
) {}
