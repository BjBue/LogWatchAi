package bbu.solution.logwatchai.domain.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AIAnalysisService {

    Optional<AIAnalysis> getAIAnalysisById(UUID aIAnalysisId);

    AIAnalysis analyze(LogEntry entry);

    CompletableFuture<AIAnalysis> analyzeAsync(LogEntry entry);

    List<AIAnalysis> getAnalysis(AIAnalysisFilter filter);
    Page<AIAnalysis> getAIAnalysisPageable(AIAnalysisFilter filter, Pageable pageable);

}
