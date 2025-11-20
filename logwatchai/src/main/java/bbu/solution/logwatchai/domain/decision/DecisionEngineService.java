package bbu.solution.logwatchai.domain.decision;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;

public interface DecisionEngineService {
    DecisionOutcome evaluate(AIAnalysis analysis);
}


