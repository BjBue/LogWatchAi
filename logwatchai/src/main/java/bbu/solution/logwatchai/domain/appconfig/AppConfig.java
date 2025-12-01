package bbu.solution.logwatchai.domain.appconfig;

import bbu.solution.logwatchai.domain.rule.AlertingConfig;
import lombok.Data;
import java.util.List;

/**
 * Represents the full application configuration as loaded from an external source
 * (typically YAML). I use this object as the central configuration model that other
 * components read from. All fields are optional, depending on what is defined in the
 * configuration file.
 */
@Data
public class AppConfig {

    /**
     * Contains all paths I should watch for log files.
     * If this list is empty or missing, no file-based log sources are activated.
     */
    private List<String> watchPaths;

    /**
     * Holds the alerting configuration block, including rule definitions.
     * If not configured, I treat the alerting configuration as empty.
     */
    private AlertingConfig alerting;

    /**
     * Holds the entire AI configuration block.
     * This may include multiple enabled and disabled models.
     */
    private AiConfig ai;

    /**
     * Defines the email address that receives generated reports.
     * If null, no report delivery via email is attempted.
     */
    private String reportEmail;

    /**
     * Encapsulates security-related configuration such as static user definitions.
     */
    private ConfigSecurity security;

}
