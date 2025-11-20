package bbu.solution.logwatchai.infrastructure.persistence.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.log.LogFilter;
import bbu.solution.logwatchai.domain.report.DailyReport;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LogEntryServiceImpl implements LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final AIAnalysisService aiAnalysisService;

    public LogEntryServiceImpl(LogEntryRepository logEntryRepository, AIAnalysisService aiAnalysisService) {
        this.logEntryRepository = logEntryRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    @Override
    public Optional<LogEntry> getLogEntryById(UUID id){
        return logEntryRepository.findById(id);
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
    @Transactional
    public AIAnalysis analyze(LogEntry logEntry) {
        // 1) call AI analysis infra service
        AIAnalysis saved = aiAnalysisService.analyze(logEntry); // aiAnalysisService ist @Autowired in der Klasse

        // 2) attach to logEntry
        logEntry.setAnalysis(saved);
        logEntry.markAsAnalyzed(saved);
        logEntryRepository.save(logEntry);

        return saved;
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
