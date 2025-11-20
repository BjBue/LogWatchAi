package bbu.solution.logwatchai.domain.decision;

import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.rule.Rule;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DecisionOutcome {

    private final List<Rule> triggeredRules;
    private final Alert alert; // one alert per analysis
}
