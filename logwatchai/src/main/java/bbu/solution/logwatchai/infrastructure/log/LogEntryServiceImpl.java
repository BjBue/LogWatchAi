package bbu.solution.logwatchai.infrastructure.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryRepository;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.log.LogFilter;
import bbu.solution.logwatchai.domain.report.DailyReport;
import bbu.solution.logwatchai.domain.source.LogSource;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LogEntryServiceImpl implements LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public LogEntryServiceImpl(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @Override
    public List<LogEntry> ingestLog(LogSource logSource) {
        return List.of();
    }

    @Override
    @Transactional
    public LogEntry saveRawLog(String rawText, UUID sourceId) {
        LogEntry entry = new LogEntry(rawText, sourceId);
        return logEntryRepository.save(entry);
    }

    @Override
    public AIAnalysis analyzeLog(LogEntry logEntry) {
        return null;
    }

    @Override
    public void analyzePendingLogs() {

    }

    @Override
    @Transactional
    public void markAsAnalyzed(UUID id) {
        logEntryRepository
                .findById(id)
                .ifPresent(logEntry -> {
                    logEntry.markAsAnalyzed();
                    logEntryRepository.save(logEntry);
                });
    }

    @Override
    public DailyReport generateReport(LocalDate date) {
        return null;
    }

    @Override
    public DailyReport generateReport(LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public List<LogEntry> getLogs(LogFilter filter) {
        return List.of();
    }

    @Override
    public Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable) {
        return null;
    }
}
