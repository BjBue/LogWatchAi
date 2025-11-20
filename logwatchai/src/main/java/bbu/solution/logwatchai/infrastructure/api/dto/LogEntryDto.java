package bbu.solution.logwatchai.infrastructure.api.dto;

import java.time.Instant;
import java.util.UUID;

public record LogEntryDto(
        UUID id,
        Instant timestamp,
        Instant ingestionTime,
        String level,
        boolean analyzed,
        boolean hasAnomaly,
        UUID sourceId,
        String rawText
) {}