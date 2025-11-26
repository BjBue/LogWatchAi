package bbu.solution.logwatchai.domain.analysis;

/**
 * Represents the severity level assigned during AI-based log analysis.
 * <p>
 * I use these values to indicate how critical a detected issue is.
 * Implementations of analytical logic, alerting, and reporting rely
 * on this classification to determine escalation or prioritization.
 * </p>
 */
public enum Severity {

    /**
     * Informational, not considered a problem.
     * I use this when the AI detected noteworthy but non-critical patterns.
     */
    INFO,

    /**
     * Low severity — minor anomaly, unusual but not harmful.
     */
    LOW,

    /**
     * Medium severity — a potentially relevant issue that should be reviewed.
     */
    MEDIUM,

    /**
     * High severity — likely a problem that requires timely attention.
     */
    HIGH,

    /**
     * Critical severity — severe issue with immediate operational risk.
     */
    CRITICAL,

    /**
     * Used when a highly critical issue is detected,
     * but the AI cannot reliably categorize it.
     * <p>
     * I use this as a fallback to indicate maximum urgency
     * when classification confidence is low.
     * </p>
     */
    UNKNOWN_CRITICAL
}
