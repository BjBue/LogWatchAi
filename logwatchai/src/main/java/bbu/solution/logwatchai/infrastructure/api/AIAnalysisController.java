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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
public class AIAnalysisController {

    private final AIAnalysisMapper mapper;
    private final AIAnalysisService aIAnalysisService;

    public AIAnalysisController(AIAnalysisMapper mapper, AIAnalysisService aIAnalysisService) {
        this.mapper = mapper;
        this.aIAnalysisService = aIAnalysisService;
    }

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

    @GetMapping("/{id}")
    public AIAnalysisDto getById(@PathVariable UUID id) {
        return aIAnalysisService.getAIAnalysisById(id).map(mapper::toDto).orElseThrow(() -> new RuntimeException("AIAnalysis Entry Not Found"));
    }
}
