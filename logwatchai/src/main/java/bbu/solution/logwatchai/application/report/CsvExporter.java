package bbu.solution.logwatchai.application.report;

import bbu.solution.logwatchai.infrastructure.api.dto.ReportDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * Component responsible for converting stored JSON report data
 * into a flat CSV representation.
 *
 * <p>I use a dedicated {@link CsvMapper} configured with JavaTime support to ensure
 * that any Instant values inside the report are serialized correctly.</p>
 */
@Component
public class CsvExporter {

    private final ObjectMapper mapper;
    private final CsvMapper csvMapper;

    /**
     * Constructs the CsvExporter with a copy of the Spring-managed ObjectMapper.
     *
     * <p>I copy the provided {@link ObjectMapper} so that I can apply local configuration
     * without affecting global Spring serialization behavior. I also initialize a
     * {@link CsvMapper} with JavaTime support to handle Instant fields in reports.</p>
     *
     * @param mapper a Spring-managed ObjectMapper instance
     */
    public CsvExporter(ObjectMapper mapper) {
        this.mapper = mapper.copy();
        this.csvMapper = CsvMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    /**
     * Converts a JSON report (as stored in the database) into a flat, single-row CSV string.
     *
     * <p>When I receive the JSON input, I first deserialize it into a {@link ReportDto}.
     * Then I build a simple CSV schema with fixed column names. Finally, I create a
     * {@link FlatCsvRow} wrapper object and let the {@link CsvMapper} serialize it into
     * a CSV string.</p>
     *
     * <p>The generated CSV always contains:</p>
     * <ul>
     *     <li>period_from</li>
     *     <li>period_to</li>
     *     <li>totalLogs</li>
     *     <li>totalAlerts</li>
     *     <li>totalAnalysis</li>
     * </ul>
     *
     * <p>If any field inside the report is null, I replace it with a safe fallback value
     * (empty string or zero).</p>
     *
     * @param json the JSON representation of a report
     * @return a single-row CSV string containing the flattened report data
     * @throws RuntimeException if parsing or CSV generation fails
     */
    public String reportJsonToCsv(String json) {
        try {
            ReportDto dto = mapper.readValue(json, ReportDto.class);

            CsvSchema schema = CsvSchema.builder()
                    .addColumn("period_from")
                    .addColumn("period_to")
                    .addColumn("totalLogs")
                    .addColumn("totalAlerts")
                    .addColumn("totalAnalysis")
                    .build()
                    .withHeader();

            FlatCsvRow row = new FlatCsvRow(dto);
            return csvMapper.writer(schema).writeValueAsString(row);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert report JSON to CSV", e);
        }
    }

    /**
     * Wrapper structure used to flatten all report fields into a single CSV row.
     *
     * <p>I ensure that missing or null values are converted into empty strings or zero
     * so that the CSV output is always well-formed and consistent.</p>
     */
    private static final class FlatCsvRow {
        public String period_from;
        public String period_to;
        public long totalLogs;
        public long totalAlerts;
        public long totalAnalysis;

        /**
         * Creates a flat CSV row from a {@link ReportDto}.
         *
         * <p>I extract the period and summary information, converting all values into
         * simple primitives or strings. If any nested components are null, I provide
         * safe fallback values to avoid serialization errors.</p>
         *
         * @param dto the report DTO to flatten
         */
        FlatCsvRow(ReportDto dto) {
            this.period_from = dto.period() == null || dto.period().from() == null
                    ? "" : dto.period().from().toString();
            this.period_to = dto.period() == null || dto.period().to() == null
                    ? "" : dto.period().to().toString();
            this.totalLogs = dto.summary() == null ? 0L : dto.summary().totalLogs();
            this.totalAlerts = dto.summary() == null ? 0L : dto.summary().totalAlerts();
            this.totalAnalysis = dto.summary() == null ? 0L : dto.summary().totalAnalysis();
        }
    }
}
