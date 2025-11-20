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

public interface LogEntryService {
    Optional<LogEntry> getLogEntryById(UUID logEntryId);
    List<LogEntry> ingestLog(LogSource logSource);
    LogEntry saveRawLog(String rawText, UUID sourceId);

    void analyzeAsync(LogEntry logEntry);
    void analyzePendingLogs();
    void markAsAnalyzed(UUID id);

    // neu: von File-Worker / Poller gerufene Hilfsmethode
    void ingestFileUpdate(LogSource source, Path filePath);

    DailyReport generateReport(LocalDate date);
    DailyReport generateReport(LocalDate from, LocalDate to);

    List<LogEntry> getLogs(LogFilter filter);
    Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable);
}
