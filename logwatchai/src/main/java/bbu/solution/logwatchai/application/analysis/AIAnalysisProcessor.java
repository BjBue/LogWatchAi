package bbu.solution.logwatchai.application.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;

public interface AIAnalysisProcessor {
    void analyzeNewLogEntry(LogEntry entry);
}
