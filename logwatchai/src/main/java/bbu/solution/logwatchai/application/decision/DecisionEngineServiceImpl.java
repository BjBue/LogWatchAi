package bbu.solution.logwatchai.application.decision;

import bbu.solution.logwatchai.application.rules.RuleEvaluator;
import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.decision.DecisionOutcome;
import bbu.solution.logwatchai.domain.decision.DecisionEngineService;
import bbu.solution.logwatchai.domain.rule.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionEngineServiceImpl implements DecisionEngineService {

    private final RuleEvaluator ruleEvaluator;
    private final AlertService alertService;

    @Override
    public DecisionOutcome evaluate(AIAnalysis analysis) {

        List<Rule> triggered = ruleEvaluator.evaluate(analysis);

        if (triggered.isEmpty()) {
            return DecisionOutcome.builder()
                    .triggeredRules(List.of())
                    .alert(null)
                    .build();
        }

        Alert alert = Alert.builder()
            .severity(analysis.getSeverity())
            .message(analysis.getSummarizedIssue())
            .ruleNames(triggered.stream().map(Rule::getName).toList())
            .sourceId(analysis.getLogEntryId())
            .build();

        Alert saved = alertService.create(alert);

        return DecisionOutcome.builder()
                .triggeredRules(triggered)
                .alert(saved)
                .build();
    }
}
