package bbu.solution.logwatchai.infrastructure.persistence.log;

import bbu.solution.logwatchai.domain.log.LogEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing LogEntry entities in the database.
 * Provides standard CRUD operations, existence checks, and custom insert behavior.
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    /**
     * Retrieves all LogEntry entities ingested after the specified timestamp,
     * sorted in ascending order by ingestion time.
     * Used for generating daily reports.
     *
     * @param since the lower bound timestamp for ingestion time
     * @return a list of LogEntry entities ingested after the given timestamp
     */
    List<LogEntry> findByIngestionTimeAfterOrderByIngestionTimeAsc(Instant since);

    /**
     * Finds a LogEntry by source ID and raw text.
     * Useful for checking duplicates when ingesting new log files.
     *
     * @param sourceId the UUID of the source
     * @param rawText the raw text of the log entry
     * @return an Optional containing the LogEntry if found, empty otherwise
     */
    Optional<LogEntry> findBySourceIdAndRawText(UUID sourceId, String rawText);

    /**
     * Checks whether a LogEntry exists for the given source ID and raw text.
     *
     * @param sourceId the UUID of the source
     * @param rawText the raw text of the log entry
     * @return true if a matching LogEntry exists, false otherwise
     */
    boolean existsBySourceIdAndRawText(UUID sourceId, String rawText);

    /**
     * Performs a native insert while ignoring duplicates using ON DUPLICATE KEY UPDATE.
     * Columns id and sourceId are stored as BINARY(16), so values are passed as byte arrays.
     *
     * @param id the UUID of the log entry as a byte array
     * @param sourceId the UUID of the source as a byte array
     * @param timestamp the original log timestamp
     * @param rawText the raw log text
     * @param level the log severity level
     * @param ingestionTime the ingestion timestamp
     * @param analyzed whether the log entry has been analyzed
     * @param hasAnomaly whether the log entry contains an anomaly
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO log_entries (
            id,
            source_id,
            timestamp,
            raw_text,
            level,
            ingestion_time,
            analyzed,
            has_anomaly
        )
        VALUES (
            :id,
            :sourceId,
            :timestamp,
            :rawText,
            :level,
            :ingestionTime,
            :analyzed,
            :hasAnomaly
        )
        ON DUPLICATE KEY UPDATE id = id
        """, nativeQuery = true)
    void insertIgnoreDuplicate(
            @Param("id") byte[] id,
            @Param("sourceId") byte[] sourceId,
            @Param("timestamp") Instant timestamp,
            @Param("rawText") String rawText,
            @Param("level") String level,
            @Param("ingestionTime") Instant ingestionTime,
            @Param("analyzed") boolean analyzed,
            @Param("hasAnomaly") boolean hasAnomaly
    );
}
