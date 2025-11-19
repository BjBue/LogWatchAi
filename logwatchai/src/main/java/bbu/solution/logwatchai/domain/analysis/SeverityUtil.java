package bbu.solution.logwatchai.domain.analysis;

public class SeverityUtil {

    private SeverityUtil() {}

    public static Severity valueOfOrNull(String s) {
        if (s == null) return Severity.INFO;
        try {
            return Severity.valueOf(s.toUpperCase());
        }
        catch (Exception ex) {
            return Severity.INFO;
        }
    }
}