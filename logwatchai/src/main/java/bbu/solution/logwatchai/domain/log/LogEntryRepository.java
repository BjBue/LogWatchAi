package bbu.solution.logwatchai.domain.log;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    List<LogEntry> findByAnalyzedFalse();

    @Query("SELECT l FROM LogEntry l WHERE l.analyzed = false AND l.ingestionTime < :cutoff")
    List<LogEntry> findUnanalyzedOlderThan(@Param("cutoff") Instant cutoff);

    Page<LogEntry> findByHasAnomalyTrue(Pageable pageable);

    Page<LogEntry> findBySourceIdAndIngestionTimeBetween(
            UUID sourceId, Instant from, Instant to, Pageable pageable);
}