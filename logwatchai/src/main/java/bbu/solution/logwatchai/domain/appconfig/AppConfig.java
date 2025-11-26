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
     * Determines whether I should enable AI-based log analysis.
     * If false, all AI analysis calls are bypassed.
     */
    private boolean aiEnabled;

    /**
     * Specifies the model identifier I should use for AI processing.
     * This may be a provider-specific model string or version.
     */
    private String aiModel;

    /**
     * Defines the email address that receives generated reports.
     * If null, no report delivery via email is attempted.
     */
    private String reportEmail;

    /**
     * Encapsulates security-related configuration such as static user definitions.
     */
    private Security security;

    /**
     * Represents the security configuration section. I use it to load static users,
     * predefined roles, or basic authentication rules depending on environment.
     */
    @Data
    public static class Security {

        /**
         * Contains the list of predefined user entries.
         * If empty, no static users are loaded.
         */
        private List<UserEntry> users;
    }

    /**
     * Represents a user definition from the configuration. I use this object to
     * construct initial in-memory users or to seed the persistence layer.
     */
    @Data
    public static class UserEntry {

        /**
         * The username of the configured user.
         */
        private String username;

        /**
         * The raw password for the configured user. I assume the caller will hash
         * or secure this value before persistence or authentication use.
         */
        private String password;

        /**
         * The list of roles assigned to the user. I treat unspecified roles as empty.
         */
        private List<String> roles;
    }
}
