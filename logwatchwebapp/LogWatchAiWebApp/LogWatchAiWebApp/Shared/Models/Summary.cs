namespace LogWatchAiWebApp.Shared.Models;

public class Summary {
    public int totalLogs { get; set; }
    public int totalAlerts { get; set; }
    public int totalAnalysis { get; set; }
    public Dictionary<string,int> logsPerSource { get; set; }
}