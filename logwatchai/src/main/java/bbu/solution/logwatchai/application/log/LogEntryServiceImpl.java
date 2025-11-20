package bbu.solution.logwatchai.application.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import bbu.solution.logwatchai.domain.decision.DecisionEngineService;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.log.LogFilter;
import bbu.solution.logwatchai.domain.report.DailyReport;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.infrastructure.persistence.log.LogEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LogEntryServiceImpl implements LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final AIAnalysisService aiAnalysisService;
    private final DecisionEngineService decisionEngineService;

    public LogEntryServiceImpl(LogEntryRepository logEntryRepository, AIAnalysisService aiAnalysisService, DecisionEngineService decisionEngineService) {
        this.logEntryRepository = logEntryRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.decisionEngineService = decisionEngineService;
    }

    @Override
    public Optional<LogEntry> getLogEntryById(UUID id){
        return logEntryRepository.findById(id);
    }

    @Override
    public List<LogEntry> ingestLog(LogSource logSource) {
        // vorhandene methoden bleiben minimal; optional später erweitern
        return List.of();
    }

    @Override
    @Transactional
    public LogEntry saveRawLog(String rawText, UUID sourceId) {
        LogEntry entry = new LogEntry(rawText, sourceId);
        return logEntryRepository.save(entry);
    }

    @Async
    @Override
    @Transactional
    public void analyzeAsync(LogEntry entry) {
        try {
            AIAnalysis ai = aiAnalysisService.analyze(entry);

            // update entry with analysis and flags
            entry.markAsAnalyzed(ai);
            logEntryRepository.save(entry);

            // decision engine will create alerts if needed
            decisionEngineService.evaluate(entry, ai);

        } catch (Exception e) {
            System.err.println("Error during async analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void analyzePendingLogs() {
        // to be implemented if needed
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
        return logEntryRepository.findAll();
    }

    @Override
    public Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable) {
        return logEntryRepository.findAll(pageable);
    }

    /**
     * Neu: ingestFileUpdate wird vom FileLogSourceWorker (oder anderen Workern) gerufen,
     * wenn eine Datei neu eingelesen bzw. gescannt werden soll.
     *
     * Diese Implementation:
     *  - liest alle Zeilen der Datei
     *  - für jede Zeile prüft sie auf Duplikat (existsBySourceIdAndRawText)
     *  - wenn nicht vorhanden -> speichert LogEntry + startet async Analyse
     *
     * Hinweis: TailReader/DirectoryWatcher erzeugen bereits einzelne Zeilen-Events und
     * rufen handleEvent() in LogWatcherServiceImpl. ingestFileUpdate ist ein zusätzlicher,
     * einfacher Adapter, den FileWorker verwenden kann.
     */
    @Override
    @Transactional
    public void ingestFileUpdate(LogSource source, Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (line == null || line.isBlank()) continue;

                // duplicate protection
                boolean exists = logEntryRepository.existsBySourceIdAndRawText(source.getId(), line);
                if (exists) continue;

                LogEntry entry = saveRawLog(line, source.getId());
                // trigger async analysis
                analyzeAsync(entry);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
