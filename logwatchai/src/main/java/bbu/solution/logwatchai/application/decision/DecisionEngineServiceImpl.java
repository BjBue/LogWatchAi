package bbu.solution.logwatchai.application.decision;

import bbu.solution.logwatchai.application.rules.RuleEvaluator;
import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.domain.alert.events.AlertCreatedEvent;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.decision.DecisionOutcome;
import bbu.solution.logwatchai.domain.decision.DecisionEngineService;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.rule.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * Decision engine implementation that evaluates AI analyses against configured rules,
 * creates alerts when rules are triggered, persists them and (optionally) sends notification emails.
 */
public class DecisionEngineServiceImpl implements DecisionEngineService {

    private final RuleEvaluator ruleEvaluator;
    private final AlertService alertService;
    private final ApplicationEventPublisher eventPublisher;

//    private final ConfigLoader configLoader;
//    private final EmailService emailService;

    /**
     * Evaluates a log entry together with its AI analysis to determine if any rules are triggered.
     * If rules are triggered, an Alert is created, persisted and — if configured — an email notification is sent.
     *
     * Steps performed:
     * 1. Execute rules against the completed AI analysis.
     * 2. If no rule is triggered, return an empty outcome (no alert).
     * 3. Construct an Alert containing severity, message, rule names and links to the log entry / source.
     * 4. Persist the Alert using the AlertService.
     * 5. If an email recipient is configured, send an alert email.
     * 6. Return the DecisionOutcome containing the triggered rules and the persisted alert.
     *
     * @param entry the original log entry that was analyzed
     * @param analysis the AIAnalysis result for the log entry
     * @return the decision outcome containing triggered rules and the created alert (if any)
     */
    @Override
    public DecisionOutcome evaluate(LogEntry entry, AIAnalysis analysis) {
        // 1. I execute all rules against the completed AI analysis
        List<Rule> triggeredRules = ruleEvaluator.evaluate(analysis);
        // 2. If no rule is triggered → then I generate no alert
        if (triggeredRules.isEmpty()) {
            return DecisionOutcome.builder()
                    .triggeredRules(List.of())
                    .alert(null)
                    .build();
        }
        // 3. I cleanly construct the alert object
        Alert alert = Alert.builder()
                .severity(analysis.getSeverity())
                .message(analysis.getSummarizedIssue())
                .ruleNames(triggeredRules.stream().map(Rule::getName).toList())
                // These two fields allow me to connect Alert → LogEntry → LogSource
                .sourceId(entry.getSourceId())
                .logEntryId(entry.getId())
                .build();
        // 4. I persist the alert
        Alert savedAlert = alertService.create(alert);
        // 5. I send the alert email via listener
        eventPublisher.publishEvent(new AlertCreatedEvent(savedAlert));
        // 6. I return the outcome
        return DecisionOutcome.builder()
                .triggeredRules(triggeredRules)
                .alert(savedAlert)
                .build();
    }
}
