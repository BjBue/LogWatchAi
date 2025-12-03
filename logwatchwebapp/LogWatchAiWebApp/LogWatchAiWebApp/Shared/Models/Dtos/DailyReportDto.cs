using System.Text.Json.Serialization;
using LogWatchAiWebApp.Shared.Models;

/// <summary>
/// Data Transfer Object (DTO) representing a daily report
/// returned by the backend API. Contains logs, alerts,
/// AI analysis, summary statistics, and top issues.
/// </summary>
public class DailyReportDto
{
    /// <summary>
    /// The reporting period covered by this daily report.
    /// </summary>
    public Period period { get; set; }

    /// <summary>
    /// Summary statistics including counts of logs, alerts, analysis,
    /// and logs per source.
    /// </summary>
    public Summary summary { get; set; }

    /// <summary>
    /// List of log entries collected during the report period.
    /// </summary>
    public List<LogEntry> logs { get; set; }

    /// <summary>
    /// List of alerts triggered during the report period.
    /// </summary>
    public List<Alert> alerts { get; set; }

    /// <summary>
    /// List of AI analysis entries generated for this report period.
    /// </summary>
    public List<AnalysisEntry> analysis { get; set; }

    /// <summary>
    /// List of top issues detected in the report, with example log entries.
    /// </summary>
    public List<TopIssue> topIssues { get; set; }

    /// <summary>
    /// Holds any additional JSON properties returned by the backend
    /// that are not explicitly defined in this DTO.
    /// </summary>
    [JsonExtensionData]
    public Dictionary<string, object?>? Extra { get; set; }
}