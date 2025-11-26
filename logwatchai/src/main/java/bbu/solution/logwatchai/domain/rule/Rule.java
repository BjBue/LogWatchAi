package bbu.solution.logwatchai.domain.rule;

import bbu.solution.logwatchai.domain.analysis.Severity;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import lombok.Getter;

import java.util.List;

@Getter
public class Rule {

    private final String name;
    private final Severity severityAtLeast;
    private final Double anomalyScoreMin;
    private final List<String> textContains;

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
     * Factory: YAML to Domain Rule
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
     * Core: fits Rule to an AIAnalysis?
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
                    (analysis.getSummarizedIssue() + " " + analysis.getLikelyCause() + " " + analysis.getRecommendation())
                            .toLowerCase();

            boolean anyMatch = textContains.stream()
                    .anyMatch(word -> combined.contains(word.toLowerCase()));

            if (!anyMatch) return false;
        }

        return true;
    }
}
