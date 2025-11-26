package bbu.solution.logwatchai.domain.analysis;

/**
 * Utility methods for converting arbitrary string inputs into {@link Severity} values.
 * <p>
 * I use this helper whenever external systems, logs, or YAML configuration
 * provide inconsistent severity keywords. It normalizes such inputs and maps
 * them to the closest {@link Severity} level.
 * </p>
 */
public final class SeverityUtil {

    private SeverityUtil() {
        // static utility class
    }

    /**
     * Converts the given string into a normalized {@link Severity} value.
     * <p>
     * I trim the input, convert it to upper case, and match several known aliases.
     * If the value cannot be recognized, I fall back to {@link Severity#UNKNOWN_CRITICAL}.
     * If the input is null or invalid, I return {@link Severity#INFO}.
     * </p>
     *
     * @param s the raw severity string, may be null
     * @return the corresponding {@link Severity}, never null
     */
    public static Severity valueOfOrNull(String s) {
        if (s == null) return Severity.INFO;

        try {
            String norm = s.trim().toUpperCase();

            return switch (norm) {
                case "INFO", "NOTICE", "DEBUG" -> Severity.INFO;
                case "LOW", "LOWER" -> Severity.LOW;
                case "WARN", "WARNING", "MEDIUM" -> Severity.MEDIUM;
                case "ERROR", "HIGH" -> Severity.HIGH;
                case "FATAL", "CRITICAL" -> Severity.CRITICAL;
                default -> Severity.UNKNOWN_CRITICAL;
            };
        }
        catch (Exception ex) {
            // safe fallback
            return Severity.INFO;
        }
    }
}
