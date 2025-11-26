package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.alert.AlertFilter;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.domain.analysis.Severity;
import bbu.solution.logwatchai.infrastructure.api.dto.AlertDto;
import bbu.solution.logwatchai.infrastructure.api.mapper.AlertMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final AlertMapper mapper;

    /**
     * Creates a new AlertController.
     *
     * @param alertService the service handling alert operations
     * @param mapper mapper converting Alert domain objects into AlertDto
     */
    public AlertController(AlertService alertService, AlertMapper mapper) {
        this.alertService = alertService;
        this.mapper = mapper;
    }

    /**
     * Retrieves alerts using optional filter parameters.
     *
     * @param id optional alert ID
     * @param createdAt optional creation timestamp
     * @param severity optional severity filter
     * @param message optional message text filter
     * @param ruleNames optional list of rule names
     * @param active optional active/inactive filter
     * @param sourceId optional log source ID
     * @param logEntryId optional log entry ID
     * @return a list of matching AlertDto objects
     */
    @GetMapping
    public List<AlertDto> getAlerts(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<String> ruleNames,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID sourceId,
            @RequestParam(required = false) UUID logEntryId
    ) {
        AlertFilter filter = new AlertFilter(
                id,
                createdAt,
                severity,
                message,
                ruleNames,
                active,
                sourceId,
                logEntryId
        );
        return alertService.getAlerts(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Retrieves paginated alerts using optional filter parameters.
     *
     * @param id optional alert ID
     * @param createdAt optional creation timestamp
     * @param severity optional severity filter
     * @param message optional message filter
     * @param ruleNames optional rule name filters
     * @param active optional active flag
     * @param sourceId optional source ID
     * @param logEntryId optional log entry ID
     * @param pageable pagination configuration
     * @return a paginated list of AlertDto objects
     */
    @GetMapping("/page")
    public Page<AlertDto> getAlertsPaged(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<String> ruleNames,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID sourceId,
            @RequestParam(required = false) UUID logEntryId,
            Pageable pageable
    ) {
        AlertFilter filter = new AlertFilter(
                id,
                createdAt,
                severity,
                message,
                ruleNames,
                active,
                sourceId,
                logEntryId
        );
        return alertService.getAlertsPageable(filter, pageable)
                .map(mapper::toDto);
    }

    /**
     * Retrieves a single alert by its ID.
     *
     * @param id the alert ID
     * @return the matching AlertDto
     * @throws RuntimeException if the alert does not exist
     */
    @GetMapping("/{id}")
    public AlertDto getById(@PathVariable UUID id) {
        return alertService.getAlertById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Log Entry Not Found"));
    }
}
