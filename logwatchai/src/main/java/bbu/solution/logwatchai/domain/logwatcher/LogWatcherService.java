package bbu.solution.logwatchai.domain.logwatcher;

public interface LogWatcherService {
    void startWatching();
    void handleEvent(LogEvent event);
}
