package bbu.solution.logwatchai.domain.report;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_reports",
        indexes = @Index(name = "idx_daily_reports_date", columnList = "reported_date", unique = true))
public class DailyReport {

    @Id @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    @Column(nullable = false)
    private Instant generatedAt = Instant.now();

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode topIssues;

    @Column(nullable = false)
    private boolean delivered = false;

    public void markDelivered() {
        this.delivered = true;
    }

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