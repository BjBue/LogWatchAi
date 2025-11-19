package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import org.springframework.data.domain.Page;
import bbu.solution.logwatchai.domain.report.DailyReport;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LogEntryService {
    List<LogEntry> ingestLog(LogSource logSource);
    LogEntry saveRawLog(String rawText, UUID sourceId);

    AIAnalysis analyze(LogEntry logEntry);
    void analyzePendingLogs();
    void markAsAnalyzed(UUID id);

    DailyReport generateReport(LocalDate date);
    DailyReport generateReport(LocalDate from, LocalDate to);

    List<LogEntry> getLogs(LogFilter filter);
    Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable);
}
