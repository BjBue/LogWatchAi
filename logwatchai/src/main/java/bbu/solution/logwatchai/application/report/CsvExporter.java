package bbu.solution.logwatchai.application.report;

import bbu.solution.logwatchai.infrastructure.api.dto.ReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class CsvExporter {

    private final ObjectMapper mapper;   // Spring-managed, already configured with JavaTimeModule
    private final CsvMapper csvMapper;

    public CsvExporter(ObjectMapper mapper) {
        // Kopie des Spring-ObjectMappers benutzen ist oft sinnvoll, damit eigene Settings möglich sind
        this.mapper = mapper.copy();
        // CSV-Mapper mit JavaTimeModule (Instant) registrieren
        this.csvMapper = CsvMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    /**
     * Konvertiert die Report-JSON (wie in DB gespeichert) in ein simples CSV (eine Zeile).
     * Nicht static — rufe per Bean auf.
     */
    public String reportJsonToCsv(String json) {
        try {
            // JSON -> DTO (ObjectMapper hat JavaTimeModule, deserialisiert Instant korrekt)
            ReportDto dto = mapper.readValue(json, ReportDto.class);

            // Erzeuge einfaches CSV-Schema (flach, eine Zeile mit Header)
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("period_from")
                    .addColumn("period_to")
                    .addColumn("totalLogs")
                    .addColumn("totalAlerts")
                    .addColumn("totalAnalysis")
                    .build()
                    .withHeader();

            // Schreibe eine einzelne flache Zeile
            FlatCsvRow row = new FlatCsvRow(dto);
            return csvMapper.writer(schema).writeValueAsString(row);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert report JSON to CSV", e);
        }
    }

    /**
     * Flache Struktur für CSV (eine Zeile). Null-sicher.
     */
    private static final class FlatCsvRow {
        public String period_from;
        public String period_to;
        public long totalLogs;
        public long totalAlerts;
        public long totalAnalysis;

        FlatCsvRow(ReportDto dto) {
            // dto.period() kann Instant-Werte enthalten — toString() ist okay für CSV
            this.period_from = dto.period() == null || dto.period().from() == null ? "" : dto.period().from().toString();
            this.period_to   = dto.period() == null || dto.period().to()   == null ? "" : dto.period().to().toString();
            this.totalLogs = dto.summary() == null ? 0L : dto.summary().totalLogs();
            this.totalAlerts = dto.summary() == null ? 0L : dto.summary().totalAlerts();
            this.totalAnalysis = dto.summary() == null ? 0L : dto.summary().totalAnalysis();
        }
    }
}
