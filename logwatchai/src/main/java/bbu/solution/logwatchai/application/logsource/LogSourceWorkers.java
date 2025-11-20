package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogSourceWorkers {

    private final FileLogSourceWorker fileWorker;

    public void startWorkerFor(LogSource source) {
        LogSourceType type = source.getType();

        switch (type) {
            case FILE -> fileWorker.start(source);
            default -> log.warn("No worker for logsource-type '{}' implemented.", type);
        }
    }
}
