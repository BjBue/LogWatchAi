package bbu.solution.logwatchai.infrastructure.persistence.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing LogSource entities in the database.
 * Provides standard CRUD operations and custom queries for active sources and path-based lookups.
 */
public interface LogSourceRepository extends JpaRepository<LogSource, UUID> {

    /**
     * Retrieves all LogSource entities that are currently marked as active.
     *
     * @return a list of active LogSource entities
     */
    List<LogSource> findByActiveTrue();

    /**
     * Finds a LogSource entity by its filesystem or network path.
     *
     * @param path the path of the log source
     * @return an Optional containing the LogSource if found, empty otherwise
     */
    Optional<LogSource> findByPath(String path);
}
