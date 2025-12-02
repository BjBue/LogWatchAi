using System.Text.Json.Serialization;
using LogWatchAiWebApp.Shared.Models;

public class DailyReportDto
{
    public Period period { get; set; }
    public Summary summary { get; set; }
    public List<LogEntry> logs { get; set; }
    public List<Alert> alerts { get; set; }
    public List<AnalysisEntry> analysis { get; set; }
    public List<TopIssue> topIssues { get; set; }

    // Falls zusätzliche Felder kommen sollten:
    [JsonExtensionData]
    public Dictionary<string, object?>? Extra { get; set; }
}