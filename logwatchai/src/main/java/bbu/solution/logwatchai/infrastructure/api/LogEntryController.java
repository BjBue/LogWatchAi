package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.log.LogFilter;
import bbu.solution.logwatchai.infrastructure.api.dto.LogEntryDto;
import bbu.solution.logwatchai.infrastructure.api.mapper.LogEntryMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller providing access to log entries.
 * Supports filtered retrieval, pagination, and fetching single entries by ID.
 */
@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final LogEntryMapper mapper;

    /**
     * Creates a new LogEntryController instance.
     *
     * @param logEntryService the service responsible for retrieving log entries
     * @param mapper the mapper converting log entry entities to DTOs
     */
    public LogEntryController(LogEntryService logEntryService, LogEntryMapper mapper) {
        this.logEntryService = logEntryService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of log entries matching the provided filter parameters.
     *
     * @param sourceId optional ID of the log source
     * @param from optional start date for filtering
     * @param to optional end date for filtering
     * @param level optional log level filter
     * @param containsText optional substring that must appear in the raw log text
     * @param analyzedOnly optional flag indicating whether only analyzed logs should be returned
     * @param hasAnomaly optional flag to filter logs containing anomalies
     * @return a list of LogEntryDto objects
     */
    @GetMapping
    public List<LogEntryDto> getLogs(
            @RequestParam(required = false) UUID sourceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String containsText,
            @RequestParam(required = false) Boolean analyzedOnly,
            @RequestParam(required = false) Boolean hasAnomaly
    ) {
        LogFilter filter = new LogFilter(
                sourceId,
                from,
                to,
                level,
                containsText,
                analyzedOnly,
                hasAnomaly
        );

        return logEntryService.getLogs(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Retrieves a paginated list of log entries matching the provided filter parameters.
     *
     * @param sourceId optional ID of the log source
     * @param from optional start date for filtering
     * @param to optional end date for filtering
     * @param level optional log level filter
     * @param containsText optional substring that must appear in the raw log text
     * @param analyzedOnly optional flag indicating whether only analyzed logs should be returned
     * @param hasAnomaly optional flag to filter logs containing anomalies
     * @param pageable pagination configuration
     * @return a Page containing LogEntryDto items
     */
    @GetMapping("/page")
    public Page<LogEntryDto> getLogsPaged(
            @RequestParam(required = false) UUID sourceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String containsText,
            @RequestParam(required = false) Boolean analyzedOnly,
            @RequestParam(required = false) Boolean hasAnomaly,
            Pageable pageable
    ) {
        LogFilter filter = new LogFilter(
                sourceId,
                from,
                to,
                level,
                containsText,
                analyzedOnly,
                hasAnomaly
        );

        return logEntryService.getLogsPageable(filter, pageable)
                .map(mapper::toDto);
    }

    /**
     * Retrieves a single log entry by its unique identifier.
     *
     * @param id the UUID of the log entry
     * @return the corresponding LogEntryDto
     * @throws RuntimeException if the entry cannot be found
     */
    @GetMapping("/{id}")
    public LogEntryDto getById(@PathVariable UUID id) {
        return logEntryService.getLogEntryById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Log Entry Not Found"));
    }
}
