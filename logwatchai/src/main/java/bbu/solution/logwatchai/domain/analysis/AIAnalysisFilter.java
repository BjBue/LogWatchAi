package bbu.solution.logwatchai.domain.analysis;

import java.time.Instant;
import java.util.UUID;

/**
 * Filter object for querying {@link AIAnalysis} entities.
 * <p>
 * This record represents optional search criteria used when filtering
 * AI-generated analyses. Any field may be {@code null}, meaning that
 * the corresponding attribute should not be used as a filter condition.
 * </p>
 *
 * @param id              optional unique identifier of the analysis
 * @param logEntryId      optional ID of the associated {@code LogEntry}
 * @param severity        optional severity classification to match
 * @param category        optional category assigned by the AI analysis
 * @param summarizedIssue optional substring or exact match for the issue summary
 * @param likelyCause     optional filter on the detected root cause description
 * @param recommendation  optional filter on the recommendation text
 * @param anomalyScore    optional minimum anomaly score threshold (0.0–1.0)
 * @param analyzedAt      optional timestamp of when the analysis was produced
 */
public record AIAnalysisFilter(
        UUID id,
        UUID logEntryId,
        Severity severity,
        String category,
        String summarizedIssue,
        String likelyCause,
        String recommendation,
        Double anomalyScore,
        Instant analyzedAt
) {}
