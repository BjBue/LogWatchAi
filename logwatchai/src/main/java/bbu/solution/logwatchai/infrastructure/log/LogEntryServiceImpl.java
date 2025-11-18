package bbu.solution.logwatchai.infrastructure.log;

import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryRepository;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogEntryServiceImpl implements LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public LogEntryServiceImpl(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @Override
    @Transactional
    public LogEntry saveRawLog(String rawText, UUID sourceId) {
        LogEntry entry = new LogEntry(rawText, sourceId);
        return logEntryRepository.save(entry);
    }

    @Override
    @Transactional
    public void markAsAnalyzed(UUID id) {
        logEntryRepository
                .findById(id)
                .ifPresent(logEntry -> {
                    logEntry.markAnalyzed();
                    logEntryRepository.save(logEntry);
                });
    }
}
