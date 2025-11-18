package bbu.solution.logwatchai.domain.analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, UUID> {

    Optional<AIAnalysis> findByLogEntryId(UUID logEntryId);

    List<AIAnalysis> findBySeverity(Severity severity);

    List<AIAnalysis> findByProbabilityGreaterThan(double threshold);
}