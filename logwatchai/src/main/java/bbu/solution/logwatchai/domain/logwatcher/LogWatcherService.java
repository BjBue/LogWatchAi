package bbu.solution.logwatchai.domain.logwatcher;

/**
 * Service interface for components that watch log sources and emit {@link LogEvent}s.
 * <p>
 * Implementations typically monitor files, streams, or external log sources
 * and forward each new log line into the ingestion pipeline.
 */
public interface LogWatcherService {

    /**
     * Starts the watching process for the underlying log source.
     * Implementations may spawn threads, register file watchers, or open streams.
     */
    void startWatching();

    /**
     * Handles a single emitted {@link LogEvent} produced by the watcher.
     *
     * @param event the log event that was detected by the watcher
     */
    void handleEvent(LogEvent event);
}
