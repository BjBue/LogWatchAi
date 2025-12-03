namespace LogWatchAiWebApp.Shared.Models.Entities
{
    /// <summary>
    /// Represents a single log entry retrieved from the backend.
    /// Contains metadata such as ingestion time, severity level, 
    /// raw log text, and source identifier.
    /// </summary>
    public class LogEntry
    {
        /// <summary>
        /// Unique identifier of the log entry.
        /// </summary>
        public string id { get; set; }

        /// <summary>
        /// Timestamp when the log entry was ingested by the system.
        /// </summary>
        public DateTime ingestionTime { get; set; }

        /// <summary>
        /// Severity level of the log entry (e.g., INFO, WARN, ERROR).
        /// </summary>
        public string level { get; set; }

        /// <summary>
        /// Raw log text as received from the source system.
        /// </summary>
        public string rawText { get; set; }

        /// <summary>
        /// Identifier of the source system that generated the log entry.
        /// </summary>
        public string sourceId { get; set; }
    }
}