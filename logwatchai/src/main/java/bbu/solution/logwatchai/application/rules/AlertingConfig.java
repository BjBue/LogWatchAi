package bbu.solution.logwatchai.application.rules;

import lombok.Data;
import java.util.List;

@Data
public class AlertingConfig {
    private List<RuleDefinition> rules;
}
