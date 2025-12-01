using System;
using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models
{
    /// <summary>
    /// DTO representing AI analysis returned by the backend.
    /// </summary>
    public class AnalysisDto
    {
        [JsonPropertyName("id")]
        public string? Id { get; set; }

        [JsonPropertyName("logEntryId")]
        public string? LogEntryId { get; set; }

        [JsonPropertyName("severity")]
        public string? Severity { get; set; }

        [JsonPropertyName("category")]
        public string? Category { get; set; }

        [JsonPropertyName("summarizedIssue")]
        public string? SummarizedIssue { get; set; }

        [JsonPropertyName("likelyCause")]
        public string? LikelyCause { get; set; }

        [JsonPropertyName("recommendation")]
        public string? Recommendation { get; set; }

        [JsonPropertyName("anomalyScore")]
        public double? AnomalyScore { get; set; }

        [JsonPropertyName("analyzedAt")]
        public DateTimeOffset? AnalyzedAt { get; set; }
    }
}