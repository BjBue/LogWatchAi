package bbu.solution.logwatchai.application.report;

import bbu.solution.logwatchai.infrastructure.api.dto.ReportDto;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.alert.Alert;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.report.DailyReport;
import bbu.solution.logwatchai.infrastructure.persistence.report.DailyReportRepository;
import bbu.solution.logwatchai.infrastructure.persistence.alert.AlertRepository;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisRepository;
import bbu.solution.logwatchai.infrastructure.persistence.log.LogEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements bbu.solution.logwatchai.domain.report.DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final LogEntryRepository logEntryRepository;
    private final AlertRepository alertRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    @Autowired
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public DailyReport generateForDate(LocalDate date) {
        // backward-compatible: generate report that covers logs since last generatedAt and set reported_date=date
        return generateLatestReport(date, false);
    }

    /**
     * Main generator used by controller:
     * - determines since = lastReport.generatedAt (or epoch)
     * - collects logs/alerts/analysis with timestamp > since
     * - builds JSON content and saves DailyReport (reported_date optional)
     */
    @Transactional
    public DailyReport generateLatestReport(LocalDate reportedDate, boolean forceEmptyDateIfNone) {

        // 1) find last report
        var last = dailyReportRepository.findAll(
                PageRequest.of(0,1, Sort.by(Sort.Direction.DESC, "generatedAt"))
        ).stream().findFirst();

        Instant since = last.map(DailyReport::getGeneratedAt).orElse(Instant.EPOCH);

        // 2) load new data since (exclusive)
        List<LogEntry> logs = logEntryRepository.findByIngestionTimeAfterOrderByIngestionTimeAsc(since);
        List<Alert> alerts = alertRepository.findByCreatedAtAfterOrderByCreatedAtAsc(since);
        List<AIAnalysis> analysis = aiAnalysisRepository.findByAnalyzedAtAfterOrderByAnalyzedAtAsc(since);

        // determine report period
        Instant from = null;
        Instant to = null;
        if (!logs.isEmpty()) {
            from = logs.get(0).getIngestionTime();
            to = logs.get(logs.size()-1).getIngestionTime();
        }
        if (!alerts.isEmpty()) {
            Instant aFrom = alerts.get(0).getCreatedAt();
            Instant aTo = alerts.get(alerts.size()-1).getCreatedAt();
            from = minInstant(from, aFrom);
            to = maxInstant(to, aTo);
        }
        if (!analysis.isEmpty()) {
            Instant anFrom = analysis.get(0).getAnalyzedAt();
            Instant anTo = analysis.get(analysis.size()-1).getAnalyzedAt();
            from = minInstant(from, anFrom);
            to = maxInstant(to, anTo);
        }

        if (from == null || to == null) {
            // nothing found: choose now as both bounds
            Instant now = Instant.now();
            from = now;
            to = now;
        }

        // build DTO lists (select fields only)
        List<ReportDto.LogItem> logItems = logs.stream()
                .map(l -> new ReportDto.LogItem(
                        l.getId().toString(),
                        l.getIngestionTime(),
                        l.getLevel(),
                        l.getRawText(),
                        Optional.ofNullable(l.getSourceId()).map(UUID::toString).orElse(null)
                )).collect(Collectors.toList());

        List<ReportDto.AlertItem> alertItems = alerts.stream()
                .map(a -> new ReportDto.AlertItem(
                        a.getId().toString(),
                        a.getCreatedAt(),
                        a.getSeverity() == null ? null : a.getSeverity().name(),
                        a.getMessage(),
                        Optional.ofNullable(a.getSourceId()).map(UUID::toString).orElse(null),
                        Optional.ofNullable(a.getLogEntryId()).map(UUID::toString).orElse(null)
                )).collect(Collectors.toList());

        List<ReportDto.AnalysisItem> analysisItems = analysis.stream()
                .map(ai -> new ReportDto.AnalysisItem(
                        ai.getId().toString(),
                        ai.getAnalyzedAt(),
                        ai.getSeverity() == null ? null : ai.getSeverity().name(),
                        ai.getAnomalyScore(),
                        ai.getSummarizedIssue(),
                        Optional.ofNullable(ai.getLogEntryId()).map(UUID::toString).orElse(null)
                )).collect(Collectors.toList());

        // summary:
        long totalLogs = logItems.size();
        long totalAlerts = alertItems.size();
        long totalanalysis = analysisItems.size();
        Map<String, Long> logsPerSource = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> Optional.ofNullable(l.getSourceId()).map(UUID::toString).orElse("unknown"),
                        Collectors.counting()
                ));

        ReportDto.Summary summary = new ReportDto.Summary(totalLogs, totalAlerts, totalanalysis, logsPerSource);

        ReportDto.Period period = new ReportDto.Period(from, to);

        // topIssues: simple top messages per source (top 5 globally)
        List<Map<String, Object>> topIssues = buildTopIssues(logs);

        // assemble report DTO
        ReportDto reportDto = new ReportDto(period, summary, logItems, alertItems, analysisItems, topIssues);

        // serialize to JSON string
        try {
            String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportDto);

            DailyReport rep = new DailyReport();
            rep.setReportedDate(reportedDate); // may be null
            rep.setContent(content);
            rep.setTopIssues(mapper.valueToTree(topIssues));

            DailyReport saved = dailyReportRepository.save(rep);
            return saved;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build report JSON", e);
        }
    }

    private Instant minInstant(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isBefore(b) ? a : b;
    }
    private Instant maxInstant(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    private List<Map<String, Object>> buildTopIssues(List<LogEntry> logs) {
        // simple aggregation: top messages (first 200 chars) globally
        Map<String, Long> freq = new HashMap<>();
        for (LogEntry l : logs) {
            String key = l.getRawText() == null ? "" : (l.getRawText().length() > 200 ? l.getRawText().substring(0,200) : l.getRawText());
            freq.merge(key, 1L, Long::sum);
        }
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("example", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void markDelivered(UUID reportId) {
        dailyReportRepository.findById(reportId).ifPresent(DailyReport::markDelivered);
    }

    @Override
    public Optional<DailyReport> getByDate(LocalDate date) {
        return dailyReportRepository.findByReportedDate(date);
    }
}
