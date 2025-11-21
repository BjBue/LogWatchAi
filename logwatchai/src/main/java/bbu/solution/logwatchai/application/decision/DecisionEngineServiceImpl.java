package bbu.solution.logwatchai.application.decision;

import bbu.solution.logwatchai.application.rules.RuleEvaluator;
import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.alert.AlertService;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.decision.DecisionOutcome;
import bbu.solution.logwatchai.domain.decision.DecisionEngineService;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.rule.Rule;
import bbu.solution.logwatchai.infrastructure.config.appconfig.ConfigLoader;
import bbu.solution.logwatchai.infrastructure.email.EmailService;
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

    private final ConfigLoader configLoader;
    private final EmailService emailService;

    @Override
    public DecisionOutcome evaluate(LogEntry entry, AIAnalysis analysis) {

        // 1. Regeln gegen die fertige AI-Analyse ausführen
        List<Rule> triggeredRules = ruleEvaluator.evaluate(analysis);

        // 2. Wenn keine Regel ausgelöst wird → kein Alert
        if (triggeredRules.isEmpty()) {
            return DecisionOutcome.builder()
                    .triggeredRules(List.of())
                    .alert(null)
                    .build();
        }

        // 3. Alert sauber ausfüllen
        Alert alert = Alert.builder()
                .severity(analysis.getSeverity())
                .message(analysis.getSummarizedIssue())
                .ruleNames(triggeredRules.stream().map(Rule::getName).toList())
                // Diese beiden Felder verbinden Alert → LogEntry → LogSource
                .sourceId(entry.getSourceId())
                .logEntryId(entry.getId())
                .build();

        // 4. Persistieren
        Alert savedAlert = alertService.create(alert);

        // 5. send email
        String recipient = configLoader.getConfig().getReportEmail();
        if(recipient != null && !recipient.isBlank()){
            emailService.sendAlertEmail(alert, recipient);
        }

        // 6. Ergebnis zurück
        return DecisionOutcome.builder()
                .triggeredRules(triggeredRules)
                .alert(savedAlert)
                .build();
    }
}
