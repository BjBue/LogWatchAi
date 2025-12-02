namespace LogWatchAiWebApp.Shared.Models;

public class Alert {
    public string id { get; set; }
    public DateTime createdAt { get; set; }
    public string severity { get; set; }
    public string message { get; set; }
    public string sourceId { get; set; }
    public string logEntryId { get; set; }
}