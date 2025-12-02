namespace LogWatchAiWebApp.Shared.Models;

public class LogEntry {
    public string id { get; set; }
    public DateTime ingestionTime { get; set; }
    public string level { get; set; }
    public string rawText { get; set; }
    public string sourceId { get; set; }
}