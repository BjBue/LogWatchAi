package bbu.solution.logwatchai.domain.rule;

import lombok.Data;
import java.util.List;

/**
 * Represents the alerting configuration used by the rule engine.
 *
 * <p>I hold a list of {@link RuleDefinition} instances, each describing a
 * single rule used to detect conditions that should trigger alerts.</p>
 *
 * <p>This class is typically populated from configuration files (e.g., YAML/JSON)
 * and injected into services responsible for evaluating incoming data against
 * the defined rules.</p>
 */
@Data
public class AlertingConfig {

    /**
     * The list of rule definitions that I load and provide to the alerting engine.
     */
    private List<RuleDefinition> rules;
}
