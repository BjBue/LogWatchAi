package bbu.solution.logwatchai.application.rules;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Adapter to expose alerting.rules from the central AppConfig (loaded by my ConfigLoader).
 * Provides null-safe accessors for AlertingConfig and rules.
 */
@Component
public class RuleConfigProperties {

    private final AppConfigService appConfigService;

    public RuleConfigProperties(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    /**
     * Returns the AlertingConfig from the AppConfig or an empty AlertingConfig if not present.
     */
    public AlertingConfig getAlerting() {
        AppConfig cfg = appConfigService.getConfig();
        if (cfg == null) return new AlertingConfig();
        AlertingConfig alerting = cfg.getAlerting();
        return alerting == null ? new AlertingConfig() : alerting;
    }

    /**
     * Returns the configured rules (or an empty list if none configured).
     */
    public List<RuleDefinition> getRules() {
        AlertingConfig a = getAlerting();
        List<RuleDefinition> rules = a.getRules();
        return rules == null ? Collections.emptyList() : Collections.unmodifiableList(rules);
    }

    /**
     * Find a rule by name (case-sensitive).
     */
    public RuleDefinition findByName(String name) {
        if (name == null) return null;
        return getRules().stream()
                .filter(r -> Objects.equals(name, r.getName()))
                .findFirst()
                .orElse(null);
    }
}
