package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import bbu.solution.logwatchai.infrastructure.persistence.logsource.LogSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link LogSourceService} responsible for managing {@link LogSource}
 * entities. This service provides full CRUD operations, activation/deactivation of sources,
 * and creation of automatically generated sources used for file-based monitoring.
 *
 * <p>All persistence operations are delegated to the underlying {@link LogSourceRepository}.</p>
 */
@Service
@RequiredArgsConstructor
public class LogSourceServiceImpl implements LogSourceService {

    private final LogSourceRepository repository;

    /**
     * Creates a new {@link LogSource} and stores it in the repository.
     *
     * @param source the log source to create
     * @return the persisted log source entity
     */
    @Override
    public LogSource create(LogSource source) {
        return repository.save(source);
    }

    /**
     * Updates an existing {@link LogSource}. The entity is overwritten based on its ID.
     *
     * @param source the log source containing updated values
     * @return the persisted updated log source
     */
    @Override
    public LogSource update(LogSource source) {
        return repository.save(source);
    }

    /**
     * Deletes a log source by its unique identifier.
     *
     * @param id the UUID of the log source to delete
     */
    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Retrieves all log sources that are currently marked as active.
     *
     * @return a list of active {@link LogSource} entities
     */
    @Override
    public List<LogSource> getActiveSources() {
        return repository.findByActiveTrue();
    }

    /**
     * Activates a log source by marking its active flag as true.
     * If the source does not exist, the operation is ignored.
     *
     * @param id the UUID of the source to activate
     */
    @Override
    public void activate(UUID id) {
        repository.findById(id).ifPresent(src -> {
            src.activate();
            repository.save(src);
        });
    }

    /**
     * Deactivates a log source by marking its active flag as false.
     * If the source does not exist, the operation is ignored.
     *
     * @param id the UUID of the source to deactivate
     */
    @Override
    public void deactivate(UUID id) {
        repository.findById(id).ifPresent(src -> {
            src.deactivate();
            repository.save(src);
        });
    }

    /**
     * Searches for a log source by its filesystem path.
     *
     * @param path the path to search for
     * @return an optional containing the found {@link LogSource}, or empty if none exists
     */
    @Override
    public Optional<LogSource> findByPath(String path) {
        return repository.findByPath(path);
    }

    /**
     * Creates a new file-based {@link LogSource} instance using the given path.
     * This method is typically used during application startup when YAML configuration
     * automatically defines a list of monitored file paths.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>I create a new {@link LogSource} with an auto-generated name.</li>
     *     <li>I set its type to FILE.</li>
     *     <li>I mark it as active by default.</li>
     *     <li>I save and return the created entity.</li>
     * </ul>
     *
     * @param path the file path that the new log source should represent
     * @return the created and persisted log source
     */
    @Override
    public LogSource createSource(String path) {
        LogSource src = new LogSource();
        src.setName("auto:" + path);
        src.setPath(path);

        try {
            src.setType(bbu.solution.logwatchai.domain.logsource.LogSourceType.FILE);
        } catch (Exception ignored) { /* should never happen */ }

        src.setActive(true);

        return repository.save(src);
    }
}
