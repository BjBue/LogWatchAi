package bbu.solution.logwatchai.infrastructure.persistence.analysis;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisFilter;
import org.springframework.data.jpa.domain.Specification;

/**
 * Provides JPA Specifications for querying AIAnalysis entities
 * based on the provided filter criteria.
 * <p>
 * Each method returns a Specification that can be combined to dynamically
 * build queries using Spring Data JPA.
 */
public class AIAnalysisSpecifications {

    /**
     * Builds a combined Specification from the provided filter.
     * Null filter fields are ignored.
     *
     * @param filter the AIAnalysisFilter containing optional filter criteria
     * @return a Specification representing all applicable filter conditions
     */
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

    /**
     * Creates a Specification filtering by ID.
     */
    private static Specification<AIAnalysis> matchesId(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.id() == null ? null : cb.equal(root.get("id"), f.id());
    }

    /**
     * Creates a Specification filtering by Log Entry ID.
     */
    private static Specification<AIAnalysis> matchesLogEntryId(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.logEntryId() == null ? null : cb.equal(root.get("logEntry").get("id"), f.logEntryId());
    }

    /**
     * Creates a Specification filtering by severity.
     */
    private static Specification<AIAnalysis> matchesSeverity(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.severity() == null ? null : cb.equal(root.get("severity"), f.severity());
    }

    /**
     * Creates a Specification filtering by category using a case-insensitive LIKE query.
     */
    private static Specification<AIAnalysis> matchesCategory(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.category() == null ? null : cb.like(cb.lower(root.get("category")),
                        "%" + f.category().toLowerCase() + "%");
    }

    /**
     * Creates a Specification filtering by summarized issue using a case-insensitive LIKE query.
     */
    private static Specification<AIAnalysis> matchesSummarizedIssue(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.summarizedIssue() == null ? null : cb.like(cb.lower(root.get("summarizedIssue")),
                        "%" + f.summarizedIssue().toLowerCase() + "%");
    }

    /**
     * Creates a Specification filtering by likely cause using a case-insensitive LIKE query.
     */
    private static Specification<AIAnalysis> matchesLikelyCause(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.likelyCause() == null ? null : cb.like(cb.lower(root.get("likelyCause")),
                        "%" + f.likelyCause().toLowerCase() + "%");
    }

    /**
     * Creates a Specification filtering by recommendation using a case-insensitive LIKE query.
     */
    private static Specification<AIAnalysis> matchesRecommendation(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.recommendation() == null ? null : cb.like(cb.lower(root.get("recommendation")),
                        "%" + f.recommendation().toLowerCase() + "%");
    }

    /**
     * Creates a Specification filtering by anomaly score, using greater than or equal comparison.
     */
    private static Specification<AIAnalysis> matchesAnomalyScore(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.anomalyScore() == null ? null : cb.greaterThanOrEqualTo(root.get("anomalyScore"), f.anomalyScore());
    }

    /**
     * Creates a Specification filtering by analyzed timestamp.
     */
    private static Specification<AIAnalysis> matchesAnalyzedAt(AIAnalysisFilter f) {
        return (root, query, cb) ->
                f.analyzedAt() == null ? null : cb.equal(root.get("analyzedAt"), f.analyzedAt());
    }
}
