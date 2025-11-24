package bbu.solution.logwatchai.infrastructure.persistence.analysis;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, UUID> {

    Optional<AIAnalysis> findByLogEntryId(UUID logEntryId);
    List<AIAnalysis> findBySeverity(Severity severity);
    List<AIAnalysis> findByAnomalyScoreGreaterThan(double threshold);

    List<AIAnalysis> findByAnalyzedAtAfterOrderByAnalyzedAtAsc(Instant since);
    List<AIAnalysis> findByAnalyzedAtBetweenOrderByAnalyzedAtAsc(Instant from, Instant to);
}