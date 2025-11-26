package bbu.solution.logwatchai.domain.rule;

import lombok.Data;
import java.util.List;

/**
 * Represents a single alerting rule definition.
 *
 * <p>I describe the conditions that must be met for a log entry to trigger this rule.
 * All fields are optional, so a rule may define any combination of conditions.</p>
 *
 * <p>I am a simple data container without behavior; evaluation is performed elsewhere
 * by the alerting or rule engine.</p>
 */
@Data
public class RuleDefinition {

    /**
     * The unique name of the rule.
     * <p>I use this name so other components can reference or look me up.</p>
     */
    private String name;

    /**
     * Optional minimum severity level required for this rule to match.
     * <p>I expect this to be a string representation (e.g., "WARN", "ERROR").</p>
     */
    private String severityAtLeast;

    /**
     * Optional minimum anomaly score for triggering this rule.
     * <p>I represent this as a string because the configuration layer may provide
     * numeric thresholds in textual form.</p>
     */
    private String anomalyScoreMin;

    /**
     * Optional list of text fragments that must be contained in a log message.
     * <p>If this list is provided, I expect the evaluator to check that
     * all or some of these fragments appear in the log content (depending
     * on how the rule engine interprets this field).</p>
     */
    private List<String> textContains;
}
