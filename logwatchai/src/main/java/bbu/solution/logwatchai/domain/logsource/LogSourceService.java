package bbu.solution.logwatchai.domain.logsource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides operations for creating, updating, managing, and querying
 * {@link LogSource} entities. Implementations of this service are responsible
 * for interacting with the persistence layer and applying business rules
 * related to log source lifecycle and lookup.
 */
public interface LogSourceService {

    /**
     * Creates and persists a new {@link LogSource}.
     *
     * @param source the log source to persist
     * @return the persisted log source including its generated ID
     */
    LogSource create(LogSource source);

    /**
     * Updates an existing {@link LogSource}.
     *
     * @param source the updated source entity
     * @return the persisted updated entity
     */
    LogSource update(LogSource source);

    /**
     * Deletes a log source by its unique identifier.
     *
     * @param id the ID of the log source to delete
     */
    void delete(UUID id);

    /**
     * Retrieves all log sources that are marked as active.
     *
     * @return a list of active log sources
     */
    List<LogSource> getActiveSources();

    /**
     * Activates a specific log source by ID.
     *
     * @param id the identifier of the source to activate
     */
    void activate(UUID id);

    /**
     * Deactivates a specific log source by ID.
     *
     * @param id the identifier of the source to deactivate
     */
    void deactivate(UUID id);

    /**
     * Searches for a log source by its configured path.
     *
     * @param path the file system or location path associated with the source
     * @return an {@link Optional} containing the matching source if found
     */
    Optional<LogSource> findByPath(String path);

    /**
     * Creates a new {@link LogSource} using only the given path.
     * Implementations may apply defaults such as type detection.
     *
     * @param path the path of the log source
     * @return the newly created log source
     */
    LogSource createSource(String path);
}
