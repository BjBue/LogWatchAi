package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.infrastructure.api.dto.AlertDto;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertDto toDto(Alert a) {
        return new AlertDto(
                a.getId(),
                a.getCreatedAt(),
                a.getSeverity(),
                a.getMessage(),
                a.getRuleNames(),
                a.isActive(),
                a.getSourceId(),
                a.getLogEntryId()
        );
    }
}
