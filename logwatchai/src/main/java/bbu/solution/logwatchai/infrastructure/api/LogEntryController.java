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

@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final LogEntryMapper mapper;

    public LogEntryController(LogEntryService logEntryService, LogEntryMapper mapper) {
        this.logEntryService = logEntryService;
        this.mapper = mapper;
    }

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

    @GetMapping("/{id}")
    public LogEntryDto getById(@PathVariable UUID id) {
        return logEntryService.getLogEntryById(id).map(mapper::toDto).orElseThrow(() -> new RuntimeException("Log Entry Not Found"));
    }
}
