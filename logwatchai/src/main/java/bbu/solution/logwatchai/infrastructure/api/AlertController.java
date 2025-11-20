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

    public AlertController(AlertService alertService, AlertMapper mapper) {
        this.alertService = alertService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AlertDto> getAlerts(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<String> ruleNames,
            @RequestParam(required = false) boolean active,
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

    @GetMapping("/page")
    public Page<AlertDto> getAlertsPaged(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Instant createdAt,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<String> ruleNames,
            @RequestParam(required = false) boolean active,
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

    @GetMapping("/{id}")
    public AlertDto getById(@PathVariable UUID id) {
        return alertService.getAlertById(id).map(mapper::toDto).orElseThrow(() -> new RuntimeException("Log Entry Not Found"));
    }
}
