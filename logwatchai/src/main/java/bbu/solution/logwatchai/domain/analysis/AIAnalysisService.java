package bbu.solution.logwatchai.domain.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;

import java.util.concurrent.CompletableFuture;

public interface AIAnalysisService {
    AIAnalysis analyze(LogEntry entry);

    CompletableFuture<AIAnalysis> analyzeAsync(LogEntry entry);
}
