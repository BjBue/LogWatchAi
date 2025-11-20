package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import bbu.solution.logwatchai.infrastructure.persistence.logsource.LogSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogSourceServiceImpl implements LogSourceService {

    private final LogSourceRepository repository;

    @Override
    public LogSource create(LogSource source) {
        return repository.save(source);
    }

    @Override
    public LogSource update(LogSource source) {
        return repository.save(source);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<LogSource> getActiveSources() {
        return repository.findByActiveTrue();
    }

    @Override
    public void activate(UUID id) {
        repository.findById(id).ifPresent(src -> {
            src.activate();
            repository.save(src);
        });
    }

    @Override
    public void deactivate(UUID id) {
        repository.findById(id).ifPresent(src -> {
            src.deactivate();
            repository.save(src);
        });
    }

    // --- neu: lookup by path ---
    @Override
    public Optional<LogSource> findByPath(String path) {
        return repository.findByPath(path);
    }

    // --- neu: create source for a file path (used on startup when yaml defines watchPaths) ---
    @Override
    public LogSource createSource(String path) {
        LogSource src = new LogSource();
        // id is @GeneratedValue on entity; do not set manually unless you prefer
        src.setName("auto:" + path);
        src.setPath(path);
        // default type = FILE
        try {
            src.setType(bbu.solution.logwatchai.domain.logsource.LogSourceType.FILE);
        } catch (Exception ignored) { /* shouldn't happen */ }
        src.setActive(true);
        return repository.save(src);
    }
}
