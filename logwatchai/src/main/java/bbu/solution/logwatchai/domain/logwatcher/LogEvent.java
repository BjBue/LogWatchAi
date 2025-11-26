package bbu.solution.logwatchai.domain.logwatcher;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a single log event emitted by a log watcher.
 * <p>
 * This class provides a minimal immutable data structure that contains:
 * <ul>
 *     <li>The file path from which the log line originated.</li>
 *     <li>The actual raw log line content.</li>
 * </ul>
 * It is typically created by file-based watchers or tailing mechanisms
 * and forwarded to the ingestion pipeline.
 */
@Data
@AllArgsConstructor
public class LogEvent {

    /** Absolute or resolved path of the log file that emitted this line. */
    private String filePath;

    /** Raw log line text as read from the file. */
    private String line;
}
