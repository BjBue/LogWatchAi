package bbu.solution.logwatchai.application.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.StringJoiner;

/**
 * Very small converter: converts the "logs" array from report JSON into CSV.
 * If you want a richer CSV, extend this class.
 */
public class CsvExporter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String reportContentToCsv(String jsonContent) {
        try {
            JsonNode root = mapper.readTree(jsonContent);
            JsonNode logs = root.path("logs");
            StringJoiner sj = new StringJoiner("\n");

            // header
            sj.add("id,ingestionTime,level,sourceId,rawText");

            if (!logs.isArray() || logs.size() == 0) {
                return sj.toString();
            }

            for (JsonNode l : logs) {
                String id = csvSafe(l.path("id").asText());
                String time = csvSafe(l.path("ingestionTime").asText());
                String level = csvSafe(l.path("level").asText());
                String source = csvSafe(l.path("sourceId").asText());
                String raw = csvSafe(l.path("rawText").asText());
                sj.add(String.join(",", id, time, level, source, raw));
            }

            return sj.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert report to CSV", e);
        }
    }

    private static String csvSafe(String s) {
        if (s == null) return "";
        String out = s.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\"") || out.contains("\n")) {
            return "\"" + out + "\"";
        }
        return out;
    }
}
