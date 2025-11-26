package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.infrastructure.api.dto.AIAnalysisDto;
import org.springframework.stereotype.Component;

/**
 * Maps {@link AIAnalysis} domain objects to {@link AIAnalysisDto} instances.
 * <p>
 * This mapper ensures that API responses never expose the domain model directly.
 * It performs a straightforward, 1:1 field transfer without modifying or enriching
 * the underlying data.
 */
@Component
public class AIAnalysisMapper {

    /**
     * Converts a domain {@link AIAnalysis} entity into an {@link AIAnalysisDto}.
     *
     * @param a the AIAnalysis domain object, must not be null
     * @return a DTO containing a serializable snapshot of the analysis
     */
    public AIAnalysisDto toDto(AIAnalysis a) {
        return new AIAnalysisDto(
                a.getId(),
                a.getLogEntryId(),
                a.getSeverity(),
                a.getCategory(),
                a.getSummarizedIssue(),
                a.getLikelyCause(),
                a.getRecommendation(),
                a.getAnomalyScore(),
                a.getAnalyzedAt()
        );
    }
}
