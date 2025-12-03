namespace LogWatchAiWebApp.Shared.Models.Entities
{
    /// <summary>
    /// Represents a summary of a daily report, including counts of logs, alerts, and analyses.
    /// </summary>
    public class Summary
    {
        /// <summary>
        /// Total number of logs in the report.
        /// </summary>
        public int totalLogs { get; set; }

        /// <summary>
        /// Total number of alerts in the report.
        /// </summary>
        public int totalAlerts { get; set; }

        /// <summary>
        /// Total number of analysis entries in the report.
        /// </summary>
        public int totalAnalysis { get; set; }

        /// <summary>
        /// Dictionary mapping source IDs to the number of logs per source.
        /// </summary>
        public Dictionary<string,int> logsPerSource { get; set; }
    }
}