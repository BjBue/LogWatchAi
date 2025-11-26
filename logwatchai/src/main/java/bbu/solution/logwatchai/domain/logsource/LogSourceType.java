package bbu.solution.logwatchai.domain.logsource;

/**
 * Enumeration of supported log source types within the system.
 * Each enum value represents a distinct ingestion mechanism that
 * determines how log data is collected and processed.
 */
public enum LogSourceType {

    /** A local or remote file that is read and tailed for updates. */
    FILE,

    /** A Syslog-compatible source (UDP/TCP RFC-compliant log stream). */
    SYSLOG,

    /** A Kafka topic used as a streaming log input. */
    KAFKA,

    /** An HTTP webhook endpoint that pushes log events. */
    HTTP_WEBHOOK,

    /** A Windows Event Log channel (e.g., Application/System/Security). */
    WINDOWS_EVENTLOG
}
