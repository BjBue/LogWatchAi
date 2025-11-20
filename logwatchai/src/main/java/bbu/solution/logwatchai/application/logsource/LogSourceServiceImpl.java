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
        Optional<LogSource> sourceOpt = repository.findById(id);
        if (sourceOpt.isPresent()) {
            LogSource src = sourceOpt.get();
            src.activate();
            repository.save(src);
        }
    }

    @Override
    public void deactivate(UUID id) {
        Optional<LogSource> sourceOpt = repository.findById(id);
        if (sourceOpt.isPresent()) {
            LogSource src = sourceOpt.get();
            src.deactivate();
            repository.save(src);
        }
    }
}
