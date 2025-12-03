namespace LogWatchAiWebApp.Shared.Models.Entities
{
    /// <summary>
    /// Represents a top issue identified in a daily report, along with its occurrence count.
    /// </summary>
    public class TopIssue
    {
        /// <summary>
        /// Example text of the issue.
        /// </summary>
        public string example { get; set; }

        /// <summary>
        /// Number of times this issue occurred.
        /// </summary>
        public int count { get; set; }
    }
}