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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportServiceImpl reportService;
    private final CsvExporter csvExporter;

    /**
     * Generates or retrieves a daily report for the given date (or today's date if none is provided)
     * and returns it in either JSON or CSV format.
     *
     * <p>Query parameters:</p>
     * <ul>
     *   <li><b>format</b> – "json" (default) or "csv"</li>
     *   <li><b>date</b> – optional LocalDate; if provided, it is used as the report’s date</li>
     * </ul>
     *
     * <p>The JSON response returns the raw JSON report content stored in the database.
     * CSV output is generated dynamically from that JSON using {@link CsvExporter}.</p>
     *
     * @param format optional output format ("json" or "csv")
     * @param date optional date specifying which report to generate or load
     * @return a JSON string or a CSV file as ResponseEntity
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
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(report.getContent());
        }
    }

    /**
     * Returns all stored daily reports.
     *
     * @return list of {@link DailyReport} entities
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(reportService.getAll());
    }
}
