package bbu.solution.logwatchai.infrastructure.api.mapper;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.infrastructure.api.dto.AlertDto;
import org.springframework.stereotype.Component;

/**
 * Maps {@link Alert} domain entities to {@link AlertDto} objects.
 * <p>
 * This mapper provides a clean separation between the internal alert model
 * and the representation exposed through the API. No transformation or
 * business logic is applied — fields are copied 1:1.
 */
@Component
public class AlertMapper {

    /**
     * Converts a domain {@link Alert} to an {@link AlertDto}.
     *
     * @param a the alert entity to map; must not be null
     * @return a DTO representing the same alert data
     */
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
