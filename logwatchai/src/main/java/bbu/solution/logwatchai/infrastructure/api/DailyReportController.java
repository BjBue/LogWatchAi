package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.application.report.CsvExporter;
import bbu.solution.logwatchai.application.report.DailyReportServiceImpl;
import bbu.solution.logwatchai.domain.report.DailyReport;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bbu.solution.logwatchai.application.report.CsvExporter;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportServiceImpl reportService;
    private final CsvExporter csvExporter;

    /**
     * Generate and return latest report (since last generatedAt).
     * ?format=json (default) or ?format=csv
     * optional reportDate param - will be stored in reported_date column
     */
    @GetMapping("/daily")
    public ResponseEntity<?> generateDailyReport(
            @RequestParam(value = "format", required = false, defaultValue = "json") String format,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate repDate = date != null ? date : LocalDate.now();
        DailyReport report = reportService.getOrCreateReport(repDate);

        if ("csv".equalsIgnoreCase(format)) {
            String csv = csvExporter.reportJsonToCsv(report.getContent());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"daily-report-" + repDate + ".csv\"");
            return ResponseEntity.ok().headers(headers).body(csv);
        } else {
            // default: return JSON as saved content
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(report.getContent());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(reportService.getAll());
    }
}