package bbu.solution.logwatchai.domain.analysis;

public class SeverityUtil {

    private SeverityUtil() {}

    public static Severity valueOfOrNull(String s) {
        if (s == null) return Severity.INFO;
        try {
            String norm = s.trim().toUpperCase();

            return switch (norm) {
                case "ERROR", "WARN", "WARNING" -> Severity.HIGH;
                case "FATAL" -> Severity.CRITICAL;
                default -> Severity.valueOf(norm);
            };
        }
        catch (Exception ex) {
            return Severity.INFO;
        }
    }
}