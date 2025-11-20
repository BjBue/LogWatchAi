package bbu.solution.logwatchai.infrastructure.api.dto;

import bbu.solution.logwatchai.domain.analysis.Severity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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