package bbu.solution.logwatchai.domain.rule;

import bbu.solution.logwatchai.domain.analysis.Severity;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import lombok.Getter;

import java.util.List;

/**
 * Represents a single rule used to determine whether an {@link AIAnalysis}
 * should trigger an alert or be otherwise flagged.
 * <p>
 * A rule can define:
 * <ul>
 *   <li>a minimum required {@link Severity}</li>
 *   <li>a minimum anomaly score</li>
 *   <li>a list of required text fragments</li>
 * </ul>
 * All configured conditions must be satisfied for the rule to match.
 */
@Getter
public class Rule {

    private final String name;
    private final Severity severityAtLeast;
    private final Double anomalyScoreMin;
    private final List<String> textContains;

    /**
     * Constructs a rule with the given criteria.
     *
     * @param name             the name of the rule
     * @param severityAtLeast  the minimum severity required (may be {@code null})
     * @param anomalyScoreMin  the minimum anomaly score required (may be {@code null})
     * @param textContains     a list of text fragments that must appear in the AI analysis (may be {@code null})
     */
    public Rule(String name,
                Severity severityAtLeast,
                Double anomalyScoreMin,
                List<String> textContains) {
        this.name = name;
        this.severityAtLeast = severityAtLeast;
        this.anomalyScoreMin = anomalyScoreMin;
        this.textContains = textContains;
    }

    /**
     * Creates a {@link Rule} instance from a YAML-based {@link RuleDefinition}.
     *
     * @param def the YAML rule definition
     * @return a new {@link Rule} mapped from the configuration
     */
    public static Rule fromDefinition(RuleDefinition def) {

        Severity sev = null;
        if (def.getSeverityAtLeast() != null) {
            sev = Severity.valueOf(def.getSeverityAtLeast().toUpperCase());
        }

        Double anomaly = null;
        if (def.getAnomalyScoreMin() != null) {
            anomaly = Double.parseDouble(def.getAnomalyScoreMin());
        }

        return new Rule(
                def.getName(),
                sev,
                anomaly,
                def.getTextContains()
        );
    }

    /**
     * Evaluates whether this rule matches the given {@link AIAnalysis}.
     * <p>
     * A rule matches only if all of its configured constraints
     * (severity, anomaly score, text fragments) are satisfied.
     *
     * @param analysis the AI analysis to evaluate
     * @return {@code true} if the rule matches the analysis; otherwise {@code false}
     */
    public boolean matches(AIAnalysis analysis) {

        // Severity check
        if (severityAtLeast != null) {
            if (analysis.getSeverity().ordinal() < severityAtLeast.ordinal()) {
                return false;
            }
        }

        // Anomaly score check
        if (anomalyScoreMin != null) {
            if (analysis.getAnomalyScore() < anomalyScoreMin) {
                return false;
            }
        }

        // Text contains check
        if (textContains != null && !textContains.isEmpty()) {
            String combined =
                    (analysis.getSummarizedIssue() + " "
                            + analysis.getLikelyCause() + " "
                            + analysis.getRecommendation())
                            .toLowerCase();

            boolean anyMatch = textContains.stream()
                    .anyMatch(word -> combined.contains(word.toLowerCase()));

            if (!anyMatch) return false;
        }

        return true;
    }
}
