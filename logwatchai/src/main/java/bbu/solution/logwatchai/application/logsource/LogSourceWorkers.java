package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The {@code LogSourceWorkers} component is responsible for delegating
 * {@link LogSource} instances to the correct worker implementation based
 * on their configured {@link LogSourceType}. Each supported log source type
 * is mapped to a dedicated worker capable of processing and monitoring it.
 *
 * <p>Currently, only the FILE log source type is implemented via
 * {@link FileLogSourceWorker}. Unsupported types yield a warning.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogSourceWorkers {

    private final FileLogSourceWorker fileWorker;

    /**
     * Starts the correct worker for the given {@link LogSource}, depending on its
     * {@link LogSourceType}. I inspect the source type and delegate the startup
     * to the appropriate worker implementation.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>If the source is of type FILE → I start the {@link FileLogSourceWorker}.</li>
     *     <li>If the type is not supported → I log a warning.</li>
     * </ul>
     *
     * <p>This method ensures extensibility: new source types can easily be integrated
     * by adding new workers and extending the switch block.</p>
     *
     * @param source the log source for which a worker should be started
     */
    public void startWorkerFor(LogSource source) {
        LogSourceType type = source.getType();

        switch (type) {
            case FILE -> fileWorker.start(source);
            default -> log.warn("No worker for logsource-type '{}' implemented.", type);
        }
    }
}
