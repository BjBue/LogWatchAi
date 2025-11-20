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

    public RuleEvaluator(RuleConfigProperties ruleConfigProperties) {

        this.rules = ruleConfigProperties.getRules().stream().map(Rule::fromDefinition).collect(Collectors.toList());
        if(this.rules.isEmpty()){
            log.warn("No alerting.rules defined in YAML");
        }
        log.info("Loaded {} alert rules from YAML config", rules.size());
    }

    /**
     * Evaluates all rules and returns all that match.
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

    public List<Rule> getRules() {
        return rules;
    }
}
