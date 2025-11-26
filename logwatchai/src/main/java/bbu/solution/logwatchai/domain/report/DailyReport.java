package bbu.solution.logwatchai.domain.report;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a generated daily report.
 * <p>
 * A {@code DailyReport} contains aggregated information about logs, alerts,
 * and AI analyses collected for a specific date. Each report is stored as JSON
 * and may optionally include a list of top issues derived from log content.
 * <p>
 * The {@code reportedDate} is unique, ensuring that only one report per day
 * can exist.
 */
@Entity
@Table(
        name = "daily_reports",
        indexes = @Index(
                name = "idx_daily_reports_date",
                columnList = "reported_date",
                unique = true
        )
)
public class DailyReport {

    /**
     * Primary key (UUID), stored as binary for optimal index efficiency.
     */
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    /**
     * The date the report covers. Each date can have exactly one report.
     */
    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    /**
     * Timestamp indicating when this report was generated.
     */
    @Column(nullable = false)
    private Instant generatedAt = Instant.now();

    /**
     * The full JSON report content, stored as LONGTEXT.
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /**
     * JSON structure containing the top issues extracted from the log data.
     */
    @Column(name = "top_issues", columnDefinition = "LONGTEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode topIssues;

    /**
     * Indicates whether this report has already been delivered to its recipients.
     */
    @Column(nullable = false)
    private boolean delivered = false;

    /** Default constructor required by JPA. */
    public DailyReport() {}

    /**
     * Full constructor for creating report instances programmatically.
     *
     * @param id           the unique identifier of the report
     * @param reportedDate the date for which the report was generated
     * @param generatedAt  the timestamp when the report was created
     * @param content      the JSON report content
     * @param topIssues    a JSON node describing the top issues
     * @param delivered    whether the report has been delivered
     */
    public DailyReport(UUID id,
                       LocalDate reportedDate,
                       Instant generatedAt,
                       String content,
                       JsonNode topIssues,
                       boolean delivered) {
        this.id = id;
        this.reportedDate = reportedDate;
        this.generatedAt = generatedAt;
        this.content = content;
        this.topIssues = topIssues;
        this.delivered = delivered;
    }

    /**
     * Marks the report as delivered.
     */
    public void markDelivered() {
        this.delivered = true;
    }

    // --- Getters / Setters (no changes made) ---

    public UUID getId() { return id; }
    public LocalDate getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDate reportedDate) { this.reportedDate = reportedDate; }
    public Instant getGeneratedAt() { return generatedAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isDelivered() { return delivered; }
    public JsonNode getTopIssues() { return topIssues; }
    public void setTopIssues(JsonNode topIssues) { this.topIssues = topIssues; }
}
