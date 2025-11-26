using System;

namespace LogWatchAiWebApp.Shared.Models
{
    public class LogEntryDto
    {
        public string Id { get; set; }
        public DateTime? Timestamp { get; set; }
        public DateTime? IngestionTime { get; set; }
        public string Level { get; set; }
        public bool? Analyzed { get; set; }
        public bool? HasAnomaly { get; set; }
        public string SourceId { get; set; }
        public string RawText { get; set; }
    }
}