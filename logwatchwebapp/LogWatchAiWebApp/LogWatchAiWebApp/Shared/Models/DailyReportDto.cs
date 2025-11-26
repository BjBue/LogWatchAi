using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models
{
    /// <summary>
    /// Represents a flexible daily report structure returned by the backend.
    /// This class uses <see cref="JsonExtensionDataAttribute"/> to capture
    /// any arbitrary JSON fields provided by the server.
    /// </summary>
    public class DailyReportDto
    {
        /// <summary>
        /// Holds all additional JSON properties returned by the backend.
        /// This allows dynamic report fields such as:
        /// <example>
        /// {
        ///   "errors": 12,
        ///   "warnings": 5,
        ///   "byService": { "svc1": 3 }
        /// }
        /// </example>
        /// </summary>
        [JsonExtensionData]
        public Dictionary<string, object?>? Data { get; set; }
    }
}