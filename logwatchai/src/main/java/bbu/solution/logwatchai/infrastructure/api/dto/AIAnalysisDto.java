package bbu.solution.logwatchai.infrastructure.api.dto;

import bbu.solution.logwatchai.domain.analysis.Severity;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing an {@code AIAnalysis} entity
 * in a form suitable for API responses.
 * <p>
 * This DTO exposes only the fields relevant for external clients and is used
 * to decouple internal domain models from the REST API layer.
 *
 * @param id             unique identifier of the AI analysis
 * @param logEntryId     ID of the {@code LogEntry} that was analyzed
 * @param severity       computed severity classification
 * @param category       AI-assigned category for the log event
 * @param summarizedIssue human-readable summary of the issue
 * @param likelyCause    explanation of the most probable cause
 * @param recommendation recommended actions suggested by the AI
 * @param anomalyScore   probability score (0.0–1.0) indicating how anomalous the entry is
 * @param analyzedAt     timestamp at which the analysis was performed
 */
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
