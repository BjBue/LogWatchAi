package bbu.solution.logwatchai.infrastructure.persistence.analysis;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing AIAnalysis entities in the database.
 * Extends JpaRepository for standard CRUD operations and
 * JpaSpecificationExecutor to support dynamic query specifications.
 *
 * Provides additional methods to retrieve analysis data based on timestamp.
 */
public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, UUID>, JpaSpecificationExecutor<AIAnalysis> {

    /**
     * Retrieves all AIAnalysis entries analyzed after the specified timestamp,
     * sorted in ascending order of analysis time.
     *
     * @param since the lower bound timestamp (exclusive) for analysis time
     * @return a list of AIAnalysis entries analyzed after the given timestamp
     */
    List<AIAnalysis> findByAnalyzedAtAfterOrderByAnalyzedAtAsc(Instant since);
}
