package bbu.solution.logwatchai.infrastructure.analysis;

import bbu.solution.logwatchai.application.analysis.AIAnalysisProcessor;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AIAnalysisProcessorImpl implements AIAnalysisProcessor {

    private final AIAnalysisServiceImpl analyzer;

    public AIAnalysisProcessorImpl(AIAnalysisServiceImpl analyzer) {
        this.analyzer = analyzer;
    }

    @Async("aiExecutor")
    @Override
    public void analyzeNewLogEntry(LogEntry entry) {
        analyzer.analyze(entry);
    }
}
