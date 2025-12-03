namespace LogWatchAiWebApp.Shared.Models.Dtos
{
    /// <summary>
    /// Represents a single log entry as provided by the backend system.
    /// Contains metadata, status information, and the raw log text.
    /// </summary>
    public class LogEntryDto
    {
        /// <summary>
        /// Gets or sets the unique identifier of the log entry.
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// Gets or sets the timestamp when the log event occurred.
        /// </summary>
        public DateTime? Timestamp { get; set; }

        /// <summary>
        /// Gets or sets the timestamp when the log entry was ingested into the system.
        /// </summary>
        public DateTime? IngestionTime { get; set; }

        /// <summary>
        /// Gets or sets the log level (e.g., INFO, WARN, ERROR).
        /// </summary>
        public string Level { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether the log entry has been analyzed.
        /// </summary>
        public bool? Analyzed { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether an anomaly was detected in the log entry.
        /// </summary>
        public bool? HasAnomaly { get; set; }

        /// <summary>
        /// Gets or sets the identifier of the source that generated the log entry.
        /// </summary>
        public string SourceId { get; set; }

        /// <summary>
        /// Gets or sets the raw log text as originally received.
        /// </summary>
        public string RawText { get; set; }
    }
}