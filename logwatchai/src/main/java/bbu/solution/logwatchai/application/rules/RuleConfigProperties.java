package bbu.solution.logwatchai.application.rules;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.domain.rule.AlertingConfig;
import bbu.solution.logwatchai.domain.rule.RuleDefinition;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides access to the alerting rule configuration stored inside the central {@link AppConfig}.
 *
 * <p>I act as an adapter so that the rule engine can easily retrieve configured alerting rules
 * without needing to directly interact with the entire application configuration object.</p>
 *
 * <p>I guarantee null-safe access, meaning I always return valid objects or empty lists even if
 * parts of the configuration are missing.</p>
 */
@Component
public class RuleConfigProperties {

    private final AppConfigService appConfigService;

    /**
     * Creates the configuration accessor. I store the {@link AppConfigService} so I can retrieve
     * the current application configuration whenever rules are requested.
     *
     * @param appConfigService the service used to obtain the current {@link AppConfig}
     */
    public RuleConfigProperties(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    /**
     * Returns the {@link AlertingConfig} section from the application configuration.
     * <p>If the configuration or alerting section is missing, I provide a new empty instance so
     * callers never have to deal with {@code null}.</p>
     *
     * @return the active {@link AlertingConfig}, never {@code null}
     */
    public AlertingConfig getAlerting() {
        AppConfig cfg = appConfigService.getConfig();
        if (cfg == null) return new AlertingConfig();
        AlertingConfig alerting = cfg.getAlerting();
        return alerting == null ? new AlertingConfig() : alerting;
    }

    /**
     * Returns the list of configured rules.
     * <p>I always return a safe, unmodifiable list. If no rules are configured, I return an empty list.</p>
     *
     * @return an immutable list of {@link RuleDefinition} objects
     */
    public List<RuleDefinition> getRules() {
        AlertingConfig a = getAlerting();
        List<RuleDefinition> rules = a.getRules();
        return rules == null ? Collections.emptyList() : Collections.unmodifiableList(rules);
    }

    /**
     * Finds a rule by its name.
     *
     * <p>I perform a case-sensitive exact match. If the name is {@code null} or no matching
     * rule exists, I return {@code null}.</p>
     *
     * @param name the rule name to look for
     * @return the matching {@link RuleDefinition}, or {@code null} if not found
     */
    public RuleDefinition findByName(String name) {
        if (name == null) return null;
        return getRules().stream()
                .filter(r -> Objects.equals(name, r.getName()))
                .findFirst()
                .orElse(null);
    }
}
