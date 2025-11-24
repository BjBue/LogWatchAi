package bbu.solution.logwatchai.application.report;

import bbu.solution.logwatchai.infrastructure.api.dto.ReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CsvExporter {

    private static final CsvMapper csvMapper = CsvMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public static String reportJsonToCsv(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // JSON → DTO
            ReportDto dto = mapper.readValue(json, ReportDto.class);

            // CSV-Schema dynamisch erzeugen (Header + Werte)
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("from")
                    .addColumn("to")
                    .addColumn("totalLogs")
                    .addColumn("totalAlerts")
                    .addColumn("totalAnalysis")
                    .build()
                    .withHeader();

            // Flaches CSV → Eine Zeile
            return csvMapper.writer(schema).writeValueAsString(
                    new FlatCsvRow(dto)
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert report JSON to CSV", e);
        }
    }

    /**
     * Flache Struktur für CSV (eine Zeile)
     */
    private record FlatCsvRow(String from, String to,
                              long totalLogs, long totalAlerts, long totalAnalysis) {

        FlatCsvRow(ReportDto dto) {
            this(
                    dto.period().from().toString(),
                    dto.period().to().toString(),
                    dto.summary().totalLogs(),
                    dto.summary().totalAlerts(),
                    dto.summary().totalAnalysis()
            );
        }
    }
}
