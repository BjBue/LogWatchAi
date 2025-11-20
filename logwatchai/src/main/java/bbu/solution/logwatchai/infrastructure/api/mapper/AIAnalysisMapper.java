package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.infrastructure.api.dto.AIAnalysisDto;
import org.springframework.stereotype.Component;

@Component
public class AIAnalysisMapper {

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
