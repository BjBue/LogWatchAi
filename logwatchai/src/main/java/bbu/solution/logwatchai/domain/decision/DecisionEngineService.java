package bbu.solution.logwatchai.domain.decision;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.log.LogEntry;

public interface DecisionEngineService {
    DecisionOutcome evaluate(LogEntry entry, AIAnalysis analysis);
}


