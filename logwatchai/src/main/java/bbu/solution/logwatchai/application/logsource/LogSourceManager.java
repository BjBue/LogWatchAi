package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The {@code LogSourceManager} is responsible for initializing and starting all active
 * log sources when the application has fully started. It retrieves enabled {@link LogSource}
 * instances from the {@link LogSourceService} and delegates worker startup to
 * {@link LogSourceWorkers}.
 *
 * <p>This component ensures that the application immediately begins consuming and monitoring
 * all log sources available at runtime, without requiring manual startup actions.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogSourceManager {

    private final LogSourceService logSourceService;
    private final LogSourceWorkers workers;

    /**
     * Triggered automatically when the Spring application is fully initialized.
     * I load all active log sources from the {@link LogSourceService} and start a
     * dedicated worker for each one.
     *
     * <p><b>Detailed Behavior:</b></p>
     * <ul>
     *     <li>I log the startup process for visibility.</li>
     *     <li>I retrieve all active log sources from persistence.</li>
     *     <li>For each retrieved log source:
     *         <ul>
     *             <li>I log which source is being started.</li>
     *             <li>I instruct {@link LogSourceWorkers} to start the correct worker for that source.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>This method is executed exactly once during the application lifecycle.</p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startAllSources() {
        log.info("LogSourceManager starting – loading active LogSources ...");

        List<LogSource> sources = logSourceService.getActiveSources();

        for (LogSource source : sources) {
            log.info("Starting LogSource '{}', type={}", source.getName(), source.getType());
            workers.startWorkerFor(source);
        }
    }
}
