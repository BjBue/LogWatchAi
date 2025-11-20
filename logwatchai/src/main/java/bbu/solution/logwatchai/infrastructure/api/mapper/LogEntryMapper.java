package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.api.dto.LogEntryDto;
import org.springframework.stereotype.Component;

@Component
public class LogEntryMapper {

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
