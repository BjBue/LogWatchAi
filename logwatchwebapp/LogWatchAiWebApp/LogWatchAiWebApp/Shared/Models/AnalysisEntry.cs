namespace LogWatchAiWebApp.Shared.Models;

public class AnalysisEntry {
    public string id { get; set; }
    public DateTime analyzedAt { get; set; }
    public string severity { get; set; }
    public double anomalyScore { get; set; }
    public string summarizedIssue { get; set; }
    public string logEntryId { get; set; }
}