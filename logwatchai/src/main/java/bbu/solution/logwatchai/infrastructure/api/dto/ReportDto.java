package bbu.solution.logwatchai.infrastructure.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ReportDto(
        Period period,
        Summary summary,
        List<LogItem> logs,
        List<AlertItem> alerts,
        List<AnalysisItem> analysis,
        List<Map<String, Object>> topIssues
) {
    public record Period(Instant from, Instant to) {}
    public record Summary(long totalLogs, long totalAlerts, long totalAnalysis, Map<String, Long> logsPerSource) {}
    public record LogItem(String id, Instant ingestionTime, String level, String rawText, String sourceId) {}
    public record AlertItem(String id, Instant createdAt, String severity, String message, String sourceId, String logEntryId) {}
    public record AnalysisItem(String id, Instant analyzedAt, String severity, double anomalyScore, String summarizedIssue, String logEntryId) {}
}
