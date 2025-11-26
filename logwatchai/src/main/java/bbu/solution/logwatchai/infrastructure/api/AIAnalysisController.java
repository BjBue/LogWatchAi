package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.analysis.AIAnalysisFilter;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import bbu.solution.logwatchai.domain.analysis.Severity;
import bbu.solution.logwatchai.infrastructure.api.dto.AIAnalysisDto;
import bbu.solution.logwatchai.infrastructure.api.mapper.AIAnalysisMapper;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
public class AIAnalysisController {

    private final AIAnalysisMapper mapper;
    private final AIAnalysisService aIAnalysisService;

    /**
     * Creates a new AIAnalysisController.
     *
     * @param mapper the mapper used to convert domain models into DTOs
     * @param aIAnalysisService the service handling AI analysis operations
     */
    public AIAnalysisController(AIAnalysisMapper mapper, AIAnalysisService aIAnalysisService) {
        this.mapper = mapper;
        this.aIAnalysisService = aIAnalysisService;
    }

    /**
     * Retrieves a list of AIAnalysis entries filtered by the provided request parameters.
     *
     * @param id optional analysis ID
     * @param logEntryId optional associated log entry ID
     * @param severity optional severity filter
     * @param category optional category filter
     * @param summarizedIssue optional summarized issue text filter
     * @param likelyCause optional likely cause text filter
     * @param recommendation optional recommendation text filter
     * @param anomalyScore optional minimum anomaly score
     * @param analyzedAt optional timestamp when the analysis occurred
     * @return a list of matching AIAnalysisDto objects
     */
    @GetMapping
    public List<AIAnalysisDto> getAnalysis(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID logEntryId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String summarizedIssue,
            @RequestParam(required = false) String likelyCause,
            @RequestParam(required = false) String recommendation,
            @RequestParam(required = false) Double anomalyScore,
            @RequestParam(required = false) Instant analyzedAt

    ) {
        AIAnalysisFilter filter = new AIAnalysisFilter(
                id,
                logEntryId,
                severity,
                category,
                summarizedIssue,
                likelyCause,
                recommendation,
                anomalyScore,
                analyzedAt
        );

        return aIAnalysisService.getAnalysis(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Retrieves paged AIAnalysis entries using optional filter parameters.
     *
     * @param id optional analysis ID
     * @param logEntryId optional associated log entry ID
     * @param severity optional severity filter
     * @param category optional category filter
     * @param summarizedIssue optional summarized issue text filter
     * @param likelyCause optional likely cause text filter
     * @param recommendation optional recommendation text filter
     * @param anomalyScore optional minimum anomaly score
     * @param analyzedAt optional analysis timestamp
     * @param pageable pagination settings
     * @return a page of AIAnalysisDto objects
     */
    @GetMapping("/page")
    public Page<AIAnalysisDto> getAIAnalysisPaged(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID logEntryId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String summarizedIssue,
            @RequestParam(required = false) String likelyCause,
            @RequestParam(required = false) String recommendation,
            @RequestParam(required = false) Double anomalyScore,
            @RequestParam(required = false) Instant analyzedAt,
            Pageable pageable
    ) {
        AIAnalysisFilter filter = new AIAnalysisFilter(
                id,
                logEntryId,
                severity,
                category,
                summarizedIssue,
                likelyCause,
                recommendation,
                anomalyScore,
                analyzedAt
        );

        return aIAnalysisService.getAIAnalysisPageable(filter, pageable)
                .map(mapper::toDto);
    }

    /**
     * Retrieves a single AIAnalysis entry by its ID.
     *
     * @param id the unique identifier of the analysis
     * @return the corresponding AIAnalysisDto
     * @throws RuntimeException if no matching analysis is found
     */
    @GetMapping("/{id}")
    public AIAnalysisDto getById(@PathVariable UUID id) {
        return aIAnalysisService.getAIAnalysisById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("AIAnalysis Entry Not Found"));
    }
}
