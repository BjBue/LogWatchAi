package bbu.solution.logwatchai.domain.log;

import java.util.UUID;

public interface LogEntryService {
    LogEntry saveRawLog(String rawText, UUID sourceId);
    void markAsAnalyzed(UUID id);
}
