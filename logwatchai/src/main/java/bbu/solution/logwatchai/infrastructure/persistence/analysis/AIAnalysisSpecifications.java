package bbu.solution.logwatchai.infrastructure.persistence.analysis;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisFilter;
import org.springframework.data.jpa.domain.Specification;

public class AIAnalysisSpecifications {

    public static Specification<AIAnalysis> applyFilter(AIAnalysisFilter filter) {

        return Specification.allOf(
                matchesId(filter),
                matchesLogEntryId(filter),
                matchesSeverity(filter),
                matchesCategory(filter),
                matchesSummarizedIssue(filter),
                matchesLikelyCause(filter),
                matchesRecommendation(filter),
                matchesAnomalyScore(filter),
                matchesAnalyzedAt(filter)
        );
    }

    private static Specification<AIAnalysis> matchesId(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.id() == null ? null : cb.equal(root.get("id"), f.id());
    }

    private static Specification<AIAnalysis> matchesLogEntryId(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.logEntryId() == null ? null : cb.equal(root.get("logEntry").get("id"), f.logEntryId());
    }

    private static Specification<AIAnalysis> matchesSeverity(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.severity() == null ? null : cb.equal(root.get("severity"), f.severity());
    }

    private static Specification<AIAnalysis> matchesCategory(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.category() == null ? null : cb.like(cb.lower(root.get("category")),
                        "%" + f.category().toLowerCase() + "%");
    }

    private static Specification<AIAnalysis> matchesSummarizedIssue(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.summarizedIssue() == null ? null : cb.like(cb.lower(root.get("summarizedIssue")),
                        "%" + f.summarizedIssue().toLowerCase() + "%");
    }

    private static Specification<AIAnalysis> matchesLikelyCause(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.likelyCause() == null ? null : cb.like(cb.lower(root.get("likelyCause")),
                        "%" + f.likelyCause().toLowerCase() + "%");
    }

    private static Specification<AIAnalysis> matchesRecommendation(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.recommendation() == null ? null : cb.like(cb.lower(root.get("recommendation")),
                        "%" + f.recommendation().toLowerCase() + "%");
    }

    private static Specification<AIAnalysis> matchesAnomalyScore(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.anomalyScore() == null ? null : cb.greaterThanOrEqualTo(root.get("anomalyScore"), f.anomalyScore());
    }

    private static Specification<AIAnalysis> matchesAnalyzedAt(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.analyzedAt() == null ? null : cb.equal(root.get("analyzedAt"), f.analyzedAt());
    }
}
