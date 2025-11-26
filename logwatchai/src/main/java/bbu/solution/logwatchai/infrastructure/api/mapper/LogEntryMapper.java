package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.api.dto.LogEntryDto;
import org.springframework.stereotype.Component;

/**
 * Maps {@link LogEntry} domain entities into {@link LogEntryDto} objects.
 * <p>
 * This mapper performs a direct 1:1 field transfer without modifying,
 * enriching, or interpreting domain data. Its purpose is to isolate the API
 * layer from internal entity structures.
 */
@Component
public class LogEntryMapper {

    /**
     * Converts a {@link LogEntry} entity to a {@link LogEntryDto}.
     *
     * @param e the log entry to convert; must not be null
     * @return a DTO representing the log entry
     */
    public LogEntryDto toDto(LogEntry e) {
        return new LogEntryDto(
                e.getId(),
                e.getTimestamp(),
                e.getIngestionTime(),
                e.getLevel(),
                e.isAnalyzed(),
                e.hasAnomaly(),
                e.getSourceId(),
                e.getRawText()
        );
    }
}
