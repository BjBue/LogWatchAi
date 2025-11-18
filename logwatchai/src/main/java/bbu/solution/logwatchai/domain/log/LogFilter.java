package bbu.solution.logwatchai.domain.log;

import java.time.LocalDate;
import java.util.UUID;

public record LogFilter(
        UUID sourceId,
        LocalDate fromDate,
        LocalDate toDate,
        String level,
        String containsText,
        Boolean analyzedOnly,
        Boolean hasAnomaly
) {}