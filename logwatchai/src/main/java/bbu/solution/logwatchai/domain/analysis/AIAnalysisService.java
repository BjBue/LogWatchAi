package bbu.solution.logwatchai.domain.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;

public interface AIAnalysisService {
    AIAnalysis analyze(LogEntry entry);
}
