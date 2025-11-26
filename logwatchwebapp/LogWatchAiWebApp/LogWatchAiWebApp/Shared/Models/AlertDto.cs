using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models
{
    public class AlertDto
    {
        [JsonPropertyName("id")]
        public string? Id { get; set; }

        [JsonPropertyName("createdAt")]
        public DateTimeOffset? CreatedAt { get; set; }

        // INFO, LOW, MEDIUM, HIGH, CRITICAL, UNKNOWN_CRITICAL
        [JsonPropertyName("severity")]
        public string? Severity { get; set; }

        [JsonPropertyName("message")]
        public string? Message { get; set; }

        [JsonPropertyName("ruleNames")]
        public List<string>? RuleNames { get; set; }

        [JsonPropertyName("active")]
        public bool? Active { get; set; }

        [JsonPropertyName("sourceId")]
        public string? SourceId { get; set; }

        [JsonPropertyName("logEntryId")]
        public string? LogEntryId { get; set; }
    }
}