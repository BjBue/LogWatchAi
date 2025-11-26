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
import java.nio.ByteBuffer;
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

    /**
     * Constructs a new LogEntryServiceImpl.
     *
     * @param logEntryRepository    the repository used to persist and query log entries
     * @param aiAnalysisService     service responsible for performing AI analyses on log entries
     * @param decisionEngineService service responsible for applying decision rules and generating alerts
     */
    public LogEntryServiceImpl(LogEntryRepository logEntryRepository, AIAnalysisService aiAnalysisService, DecisionEngineService decisionEngineService) {
        this.logEntryRepository = logEntryRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.decisionEngineService = decisionEngineService;
    }

    /**
     * Retrieves a LogEntry by its identifier.
     *
     * @param id the UUID of the log entry
     * @return an Optional containing the LogEntry if found, otherwise empty
     */
    @Override
    public Optional<LogEntry> getLogEntryById(UUID id){
        return logEntryRepository.findById(id);
    }

    /**
     * Placeholder for ingesting logs from a LogSource. Current implementation returns an empty list.
     *
     * @param logSource the source to ingest logs from
     * @return a list of ingested LogEntry objects (currently empty)
     */
    @Override
    public List<LogEntry> ingestLog(LogSource logSource) {
        return List.of();
    }

    /**
     * Checks whether a log entry already exists for the given source and raw text.
     *
     * @param sourceId the UUID of the log source
     * @param rawText  the raw log line text
     * @return true if an entry exists, false otherwise
     */
    @Override
    public boolean doesLogEntryExistsBySourceIdRawText(UUID sourceId, String rawText){
        return logEntryRepository.existsBySourceIdAndRawText(sourceId, rawText);
    }

    /**
     * Saves a raw log entry into the repository using an insert-ignore-duplicate strategy.
     * If an identical entry already exists it will not create a duplicate; the existing entry is returned if present.
     *
     * @param rawText  the raw log line text
     * @param sourceId the UUID of the log source
     * @return the saved or existing LogEntry
     */
    @Override
    @Transactional
    public LogEntry saveRawLog(String rawText, UUID sourceId) {
        LogEntry entry = new LogEntry(rawText, sourceId);

        logEntryRepository.insertIgnoreDuplicate(
                uuidToBytes(entry.getId()),
                uuidToBytes(entry.getSourceId()),
                entry.getTimestamp(),
                entry.getRawText(),
                entry.getLevel(),
                entry.getIngestionTime(),
                entry.isAnalyzed(),
                entry.hasAnomaly()
        );

        return logEntryRepository
                .findBySourceIdAndRawText(sourceId, rawText)
                .orElse(entry);
    }

    /**
     * Converts a UUID to a 16-byte array representation suitable for binary storage.
     *
     * @param uuid the UUID to convert
     * @return a 16-byte array representing the UUID
     */
    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Asynchronously performs AI analysis for the provided LogEntry.
     *
     * Steps executed:
     * 1. I reload the latest entity to avoid working on a stale detached instance.
     * 2. I skip analysis if another thread already processed this entry.
     * 3. I call the AI analysis service to produce an AIAnalysis object.
     * 4. I mark the entry as analyzed and persist the updated entity.
     * 5. I invoke the decision engine to evaluate the analysis and possibly create alerts.
     *
     * Any exceptions are caught and printed to stderr to avoid crashing the async executor.
     *
     * @param entry the log entry to analyze asynchronously
     */
    @Async
    @Override
    public void analyzeAsync(LogEntry entry) {
        try {
            // I reload the latest entity so I don't work with stale data
            var maybe = logEntryRepository.findById(entry.getId());
            if (maybe.isEmpty()) return;
            LogEntry current = maybe.get();

            // I skip analysis if another thread already processed it
            if (current.isAnalyzed()) {
                return; // debug log possible here
            }

            AIAnalysis ai = aiAnalysisService.analyze(current);

            current.markAsAnalyzed(ai);
            logEntryRepository.save(current);

            decisionEngineService.evaluate(current, ai);

        } catch (Exception e) {
            System.err.println("Error during async analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Triggers analysis on pending logs.
     *
     * Note: This method is a placeholder and currently not implemented.
     */
    @Override
    public void analyzePendingLogs() {
        // to be implemented if needed
    }

    /**
     * Marks a log entry as analyzed by its ID.
     *
     * @param id the UUID of the log entry to mark as analyzed
     */
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

    /**
     * Generates a daily report for the given date.
     *
     * Note: This method currently returns null and needs implementation.
     *
     * @param date the date for which to generate the report
     * @return the generated DailyReport or null if unimplemented
     */
    @Override
    public DailyReport generateReport(LocalDate date) {
        return null;
    }

    /**
     * Generates a report for the given date range.
     *
     * Note: This method currently returns null and needs implementation.
     *
     * @param from the start date (inclusive)
     * @param to   the end date (inclusive)
     * @return the generated DailyReport or null if unimplemented
     */
    @Override
    public DailyReport generateReport(LocalDate from, LocalDate to) {
        return null;
    }

    /**
     * Retrieves logs that match the provided filter.
     *
     * @param filter filtering criteria for logs
     * @return a list of matching LogEntry objects
     */
    @Override
    public List<LogEntry> getLogs(LogFilter filter) {
        return logEntryRepository.findAll();
    }

    /**
     * Retrieves logs in a pageable form that match the provided filter.
     *
     * @param filter   filtering criteria for logs
     * @param pageable paging and sorting information
     * @return a Page of LogEntry objects
     */
    @Override
    public Page<LogEntry> getLogsPageable(LogFilter filter, Pageable pageable) {
        return logEntryRepository.findAll(pageable);
    }

    /**
     * Ingests updates from a file by reading each line, saving it as a LogEntry,
     * and triggering asynchronous AI analysis for each new line.
     *
     * This method performs the following steps:
     * 1. I read the file line by line.
     * 2. I ignore empty or blank lines.
     * 3. I save each line as a raw LogEntry (using insert-ignore semantics to avoid duplicates).
     * 4. I trigger asynchronous AI analysis for every stored log line.
     * 5. If file access fails, I log the problem to stderr.
     *
     * @param source   the LogSource containing metadata about where the file originates
     * @param filePath the path to the file whose contents should be ingested
     */
    @Override
    @Transactional
    public void ingestFileUpdate(LogSource source, Path filePath) {
        try {
            // I read the entire file into a list of lines
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                // I skip empty or whitespace-only lines because they cannot form meaningful log entries
                if (line == null || line.isBlank()) continue;

                // I save the raw log line for the specific source.
                // If an identical entry already exists, the insertIgnoreDuplicate(...) logic prevents duplication.
                LogEntry entry = saveRawLog(line, source.getId());

                // I trigger the asynchronous AI analysis pipeline for this log line.
                // This includes: AI analysis → persisting enriched entry → decision engine evaluation → optional alerts.
                analyzeAsync(entry);
            }
        } catch (IOException e) {
            // I log the failure if reading the file was not possible
            System.err.println("Failed to read file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
