package bbu.solution.logwatchai.infrastructure.api.dto;

import bbu.solution.logwatchai.domain.analysis.Severity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for exposing {@code Alert} information
 * through the API layer.
 * <p>
 * This DTO provides a clean, serialization-friendly representation
 * of alert data without leaking domain internals or JPA entities.
 *
 * @param id          unique identifier of the alert
 * @param createdAt   timestamp when the alert was created
 * @param severity    severity level associated with the alert
 * @param message     human-readable message describing the alert
 * @param ruleNames   names of rules that triggered this alert
 * @param active      indicates whether the alert is still active
 * @param sourceId    ID of the related log source
 * @param logEntryId  ID of the related log entry
 */
public record AlertDto(
        UUID id,
        Instant createdAt,
        Severity severity,
        String message,
        List<String> ruleNames,
        boolean active,
        UUID sourceId,
        UUID logEntryId
) {}
