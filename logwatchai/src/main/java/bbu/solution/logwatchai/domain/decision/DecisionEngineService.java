package bbu.solution.logwatchai.domain.decision;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.log.LogEntry;

/**
 * Defines the contract for the decision engine.
 * I evaluate a log entry together with its AI-based analysis and
 * produce a {@link DecisionOutcome} describing what the system should do.
 */
public interface DecisionEngineService {

    /**
     * Evaluates the given log entry and its associated AI analysis.
     * I use both the raw log information and the enriched AI result
     * to determine whether an alert should be triggered, ignored, or
     * handled with a specific action defined by the system's rules.
     *
     * @param entry    the raw log entry I am evaluating
     * @param analysis the AI analysis result associated with the log entry
     * @return a {@link DecisionOutcome} describing the final decision
     */
    DecisionOutcome evaluate(LogEntry entry, AIAnalysis analysis);
}
