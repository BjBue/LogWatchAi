package bbu.solution.logwatchai.domain.decision;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.rule.Rule;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Represents the outcome of my decision-making process for a single log entry.
 * I return this object after evaluating the log entry together with its AI analysis.
 * <p>
 * A decision outcome consists of:
 * <ul>
 *     <li>the list of rules I determined to be triggered</li>
 *     <li>an optional alert that I created as a result of the triggered rules</li>
 * </ul>
 * I always evaluate exactly one log entry and therefore at most one alert is produced.
 */
@Getter
@Builder
public class DecisionOutcome {

    /**
     * The list of rules that I identified as triggered during my evaluation.
     * If this list is empty, it means that no rule matched the input.
     */
    private final List<Rule> triggeredRules;

    /**
     * The alert I created based on the triggered rules.
     * This is {@code null} if none of the rules resulted in an alert.
     */
    private final Alert alert; // one alert per analysis
}
