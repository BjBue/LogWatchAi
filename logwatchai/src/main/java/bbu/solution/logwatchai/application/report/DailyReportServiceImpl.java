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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating and managing {@link DailyReport} entities.
 *
 * <p>I collect new logs, alerts, and AI analysis results since the last generated
 * report and aggregate them into a structured {@link ReportDto}, which I then serialize
 * into JSON and store inside a {@link DailyReport}.</p>
 */
@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements bbu.solution.logwatchai.domain.report.DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final LogEntryRepository logEntryRepository;
    private final AlertRepository alertRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    @Autowired
    private final ObjectMapper mapper;

    /**
     * Generates a report for a specific date by delegating to {@link #generateLatestReport(LocalDate, boolean)}.
     *
     * <p>I keep backward compatibility by generating a report that covers logs collected
     * since the previous report, but I explicitly set the report's date to the provided value.</p>
     *
     * @param date the date for which I generate the report
     * @return the generated {@link DailyReport}
     */
    @Override
    @Transactional
    public DailyReport generateForDate(LocalDate date) {
        return generateLatestReport(date, false);
    }

    /**
     * Generates a new report based on all new logs, alerts, and analysis results since the last generated report.
     *
     * <p>I perform the following steps:</p>
     * <ol>
     *     <li>Determine the timestamp of the last report (or use epoch if none exists).</li>
     *     <li>Load all logs, alerts, and AI analyses created after that timestamp.</li>
     *     <li>Compute the time window (period) covered by this report.</li>
     *     <li>Convert all domain objects into flattened DTO structures.</li>
     *     <li>Build the {@link ReportDto} with summary data and top issues.</li>
     *     <li>Serialize the DTO into JSON and store it inside a new {@link DailyReport}.</li>
     * </ol>
     *
     * <p>If no new data is available, I fall back to a period where both "from" and "to" timestamps
     * equal the current time, ensuring that the report remains well-formed.</p>
     *
     * @param reportedDate   the date to assign to the report (may be null)
     * @param forceEmptyDateIfNone unused compatibility flag
     * @return the saved {@link DailyReport}
     */
    @Transactional
    public DailyReport generateLatestReport(LocalDate reportedDate, boolean forceEmptyDateIfNone) {

        var last = dailyReportRepository.findAll(
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "generatedAt"))
        ).stream().findFirst();

        Instant since = last.map(DailyReport::getGeneratedAt).orElse(Instant.EPOCH);

        List<LogEntry> logs = logEntryRepository.findByIngestionTimeAfterOrderByIngestionTimeAsc(since);
        List<Alert> alerts = alertRepository.findByCreatedAtAfterOrderByCreatedAtAsc(since);
        List<AIAnalysis> analysis = aiAnalysisRepository.findByAnalyzedAtAfterOrderByAnalyzedAtAsc(since);

        Instant from = null;
        Instant to = null;

        if (!logs.isEmpty()) {
            from = logs.get(0).getIngestionTime();
            to = logs.get(logs.size() - 1).getIngestionTime();
        }
        if (!alerts.isEmpty()) {
            Instant aFrom = alerts.get(0).getCreatedAt();
            Instant aTo = alerts.get(alerts.size() - 1).getCreatedAt();
            from = minInstant(from, aFrom);
            to = maxInstant(to, aTo);
        }
        if (!analysis.isEmpty()) {
            Instant anFrom = analysis.get(0).getAnalyzedAt();
            Instant anTo = analysis.get(analysis.size() - 1).getAnalyzedAt();
            from = minInstant(from, anFrom);
            to = maxInstant(to, anTo);
        }

        if (from == null || to == null) {
            Instant now = Instant.now();
            from = now;
            to = now;
        }

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

        long totalLogs = logItems.size();
        long totalAlerts = alertItems.size();
        long totalAnalysis = analysisItems.size();

        Map<String, Long> logsPerSource = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> Optional.ofNullable(l.getSourceId()).map(UUID::toString).orElse("unknown"),
                        Collectors.counting()
                ));

        ReportDto.Summary summary = new ReportDto.Summary(totalLogs, totalAlerts, totalAnalysis, logsPerSource);
        ReportDto.Period period = new ReportDto.Period(from, to);
        List<Map<String, Object>> topIssues = buildTopIssues(logs);

        ReportDto reportDto = new ReportDto(period, summary, logItems, alertItems, analysisItems, topIssues);

        try {
            String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportDto);

            DailyReport rep = new DailyReport();
            rep.setReportedDate(reportedDate);
            rep.setContent(content);
            rep.setTopIssues(mapper.valueToTree(topIssues));

            return dailyReportRepository.save(rep);

        } catch (Exception e) {
            throw new RuntimeException("Failed to build report JSON", e);
        }
    }

    /**
     * Returns the earliest of the two given instants, handling null values safely.
     */
    private Instant minInstant(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isBefore(b) ? a : b;
    }

    /**
     * Returns the latest of the two given instants, handling null values safely.
     */
    private Instant maxInstant(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    /**
     * Builds a list of the top most frequent log message fragments (up to 200 characters).
     *
     * <p>I aggregate messages globally across all sources and return the
     * top 10 entries, each containing the message example and the frequency.</p>
     */
    private List<Map<String, Object>> buildTopIssues(List<LogEntry> logs) {
        Map<String, Long> freq = new HashMap<>();
        for (LogEntry l : logs) {
            String key = l.getRawText() == null
                    ? ""
                    : (l.getRawText().length() > 200
                    ? l.getRawText().substring(0, 200)
                    : l.getRawText());
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

    /**
     * Marks a report as delivered by invoking {@link DailyReport#markDelivered()}.
     *
     * @param reportId the ID of the report to mark as delivered
     */
    @Override
    public void markDelivered(UUID reportId) {
        dailyReportRepository.findById(reportId).ifPresent(DailyReport::markDelivered);
    }

    /**
     * Retrieves a report by its assigned reported date.
     *
     * @param date the date for which I search for a report
     * @return an optional containing the report if present
     */
    @Override
    public Optional<DailyReport> getByDate(LocalDate date) {
        return dailyReportRepository.findByReportedDate(date);
    }

    /**
     * Retrieves a report for the given date, creating it if necessary.
     *
     * <p>If a race condition occurs (multiple threads generating reports simultaneously),
     * I catch the integrity exception and simply fetch the report that the other thread saved.</p>
     *
     * @param date the date for which I retrieve or create a report
     * @return the report for the given date
     */
    @Override
    @Transactional
    public DailyReport getOrCreateReport(LocalDate date) {
        Optional<DailyReport> existing = dailyReportRepository.findByReportedDate(date);
        if (existing.isPresent()) {
            return existing.get();
        }

        try {
            return generateLatestReport(date, false);
        } catch (DataIntegrityViolationException ex) {
            return dailyReportRepository.findByReportedDate(date)
                    .orElseThrow(() -> ex);
        }
    }

    /**
     * Returns all daily reports sorted by reported date in descending order.
     *
     * @return a list of all reports
     */
    @Override
    public List<DailyReport> getAll() {
        return dailyReportRepository.findAll(Sort.by("reportedDate").descending());
    }
}
