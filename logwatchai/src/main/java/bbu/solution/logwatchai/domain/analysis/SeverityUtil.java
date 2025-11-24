package bbu.solution.logwatchai.domain.analysis;

public class SeverityUtil {

    private SeverityUtil() {}

    public static Severity valueOfOrNull(String s) {
        if (s == null) return Severity.INFO;
        try {
            String norm = s.trim().toUpperCase();

            return switch(norm){
                case "INFO", "NOTICE", "DEBUG" -> Severity.INFO;
                case "LOW", "LOWER" -> Severity.LOW;
                case "WARN", "WARNING", "MEDIUM" -> Severity.MEDIUM;
                case "ERROR", "HIGH" -> Severity.HIGH;
                case "FATAL", "CRITICAL" -> Severity.CRITICAL;
                default -> Severity.UNKNOWN_CRITICAL;
            };
        }
        catch (Exception ex) {
            return Severity.INFO;
        }
    }
}