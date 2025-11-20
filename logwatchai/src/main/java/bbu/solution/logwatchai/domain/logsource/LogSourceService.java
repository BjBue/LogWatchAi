package bbu.solution.logwatchai.domain.logsource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LogSourceService {
    LogSource create(LogSource source);
    LogSource update(LogSource source);
    void delete(UUID id);
    List<LogSource> getActiveSources();
    void activate(UUID id);
    void deactivate(UUID id);
    Optional<LogSource> findByPath(String path);
    LogSource createSource(String path);
}