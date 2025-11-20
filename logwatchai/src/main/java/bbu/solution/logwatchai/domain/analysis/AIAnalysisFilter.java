package bbu.solution.logwatchai.domain.analysis;

import java.time.Instant;
import java.util.UUID;

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