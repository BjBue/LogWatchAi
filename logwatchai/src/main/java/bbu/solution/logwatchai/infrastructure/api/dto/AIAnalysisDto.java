package bbu.solution.logwatchai.infrastructure.api.dto;

import bbu.solution.logwatchai.domain.analysis.Severity;

import java.time.Instant;
import java.util.UUID;

public record AIAnalysisDto(
        UUID id,
        UUID logEntryId,
        Severity severity,
        String category,
        String summarizedIssue,
        String likelyCause,
        String recommendation,
        double anomalyScore,
        Instant analyzedAt
) {}