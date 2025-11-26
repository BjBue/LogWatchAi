package bbu.solution.logwatchai.application.rules;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.rule.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RuleEvaluator {

    private final List<Rule> rules;

    /**
     * Creates a new instance of the rule evaluator.
     *
     * <p>I load all rule definitions from the provided {@link RuleConfigProperties}
     * and immediately convert them into executable {@link Rule} objects.
     * By doing this in the constructor, I ensure that the evaluator is fully
     * initialized and ready for rule checks as soon as it is used.</p>
     *
     * <p>If no rules are configured, I log a warning because it means
     * the alerting subsystem will never trigger anything.</p>
     *
     * @param ruleConfigProperties the configuration adapter that provides rule definitions
     */
    public RuleEvaluator(RuleConfigProperties ruleConfigProperties) {
        this.rules = ruleConfigProperties.getRules().stream()
                .map(Rule::fromDefinition)
                .collect(Collectors.toList());

        if (this.rules.isEmpty()) {
            log.warn("No alerting.rules defined in YAML");
        }

        log.info("Loaded {} alert rules from YAML config", rules.size());
    }

    /**
     * Evaluates all configured rules against the given analysis result.
     *
     * <p>I iterate through every rule and ask it whether it matches the provided
     * {@link AIAnalysis}. Whenever a rule reports a match, I log a debug message
     * so the caller can understand why an alert was triggered.</p>
     *
     * <p>I return a list of all rules that matched the analysis.</p>
     *
     * @param analysis the analysis result that I use for rule evaluation
     * @return all rules that match this analysis
     */
    public List<Rule> evaluate(AIAnalysis analysis) {
        return rules.stream()
                .filter(rule -> {
                    boolean match = rule.matches(analysis);
                    if (match) {
                        log.debug("Rule '{}' triggered", rule.getName());
                    }
                    return match;
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns all rules that I currently manage.
     *
     * <p>This method is intentionally simple; I expose the internal list so that
     * other components can inspect which rules are active.</p>
     *
     * @return the list of loaded rule objects
     */
    public List<Rule> getRules() {
        return rules;
    }
}
