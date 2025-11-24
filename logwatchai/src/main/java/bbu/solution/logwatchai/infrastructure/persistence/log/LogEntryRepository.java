package bbu.solution.logwatchai.infrastructure.persistence.log;

import bbu.solution.logwatchai.domain.log.LogEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    List<LogEntry> findByAnalyzedFalse();

    @Query("SELECT l FROM LogEntry l WHERE l.analyzed = false AND l.ingestionTime < :cutoff")
    List<LogEntry> findUnanalyzedOlderThan(@Param("cutoff") Instant cutoff);

    Page<LogEntry> findByHasAnomalyTrue(Pageable pageable);

    Page<LogEntry> findBySourceIdAndIngestionTimeBetween(
            UUID sourceId, Instant from, Instant to, Pageable pageable);

    // for dailyReports
    List<LogEntry> findByIngestionTimeAfterOrderByIngestionTimeAsc(Instant since);
    List<LogEntry> findByIngestionTimeBetweenOrderByIngestionTimeAsc(Instant from, Instant to);

    // new: simple existence check to avoid duplicate entries when ingesting files
    Optional<LogEntry> findBySourceIdAndRawText(UUID sourceId, String rawText);
    boolean existsBySourceIdAndRawText(UUID sourceId, String rawText);

    /**
     * Native insert that ignores duplicates via ON DUPLICATE KEY UPDATE id = id.
     * Note: id and sourceId are passed as byte[] (16 bytes) because columns are BINARY(16).
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
