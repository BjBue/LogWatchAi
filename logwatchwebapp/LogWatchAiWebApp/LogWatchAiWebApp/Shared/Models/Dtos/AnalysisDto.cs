using System;
using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models.Dtos
{
    /// <summary>
    /// Data Transfer Object (DTO) representing an AI analysis entry
    /// returned from the backend API.
    /// </summary>
    public class AnalysisDto
    {
        /// <summary>
        /// Unique identifier of the analysis entry.
        /// </summary>
        [JsonPropertyName("id")]
        public string? Id { get; set; }

        /// <summary>
        /// Identifier of the associated log entry.
        /// </summary>
        [JsonPropertyName("logEntryId")]
        public string? LogEntryId { get; set; }

        /// <summary>
        /// Severity level of the analysis (e.g., INFO, LOW, MEDIUM, HIGH, CRITICAL).
        /// </summary>
        [JsonPropertyName("severity")]
        public string? Severity { get; set; }

        /// <summary>
        /// Category assigned to this analysis entry.
        /// </summary>
        [JsonPropertyName("category")]
        public string? Category { get; set; }

        /// <summary>
        /// Summarized description of the issue detected by the AI analysis.
        /// </summary>
        [JsonPropertyName("summarizedIssue")]
        public string? SummarizedIssue { get; set; }

        /// <summary>
        /// Most likely cause identified by the AI analysis.
        /// </summary>
        [JsonPropertyName("likelyCause")]
        public string? LikelyCause { get; set; }

        /// <summary>
        /// Recommended action or remediation for this issue.
        /// </summary>
        [JsonPropertyName("recommendation")]
        public string? Recommendation { get; set; }

        /// <summary>
        /// Anomaly score indicating the significance or unusualness of this analysis entry.
        /// </summary>
        [JsonPropertyName("anomalyScore")]
        public double? AnomalyScore { get; set; }

        /// <summary>
        /// Timestamp when the analysis was performed.
        /// </summary>
        [JsonPropertyName("analyzedAt")]
        public DateTimeOffset? AnalyzedAt { get; set; }
    }
}
