namespace LogWatchAiWebApp.Shared.Models.Entities
{
    /// <summary>
    /// Represents a time period with a start and end timestamp.
    /// </summary>
    public class Period
    {
        /// <summary>
        /// Start timestamp of the period.
        /// </summary>
        public DateTime from { get; set; }

        /// <summary>
        /// End timestamp of the period.
        /// </summary>
        public DateTime to { get; set; }
    }
}