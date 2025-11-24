package bbu.solution.logwatchai.domain.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ai_analysis",
        indexes = {
                @Index(name = "idx_ai_analysis_log_entry_id", columnList = "logEntryId"),
                @Index(name = "idx_ai_analysis_severity", columnList = "severity"),
                @Index(name = "idx_ai_analysis_anomaly_score", columnList = "anomaly_score"),
                @Index(name = "idx_ai_analysis_analyzed_at", columnList = "analyzedAt")
        }
)
public class AIAnalysis {

    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)", updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "log_entry_id", nullable = false, updatable = false, columnDefinition = "BINARY(16)", unique = true)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID logEntryId;

    // 1:1 Beziehung zurück zur LogEntry (optional, aber sehr praktisch für Navigation)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_entry_id", insertable = false, updatable = false)
    private LogEntry logEntry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String summarizedIssue;

    @Column(columnDefinition = "TEXT")
    private String likelyCause;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "anomaly_score", nullable = false)
    private double anomalyScore; // 0.0 – 1.0

    @Column(nullable = false, updatable = false)
    private Instant analyzedAt = Instant.now();

    // ==================== Konstruktoren ====================
    public AIAnalysis() {}

    public AIAnalysis(UUID logEntryId, Severity severity, String category,
                      String summarizedIssue, String likelyCause,
                      String recommendation, double probability) {
        this.logEntryId = logEntryId;
        this.severity = severity;
        this.category = category;
        this.summarizedIssue = summarizedIssue;
        this.likelyCause = likelyCause;
        this.recommendation = recommendation;
        this.anomalyScore = Math.min(Math.max(probability, 0.0), 1.0); // clamp 0–1
    }

    public String summarizedIssue() {
        return summarizedIssue != null ? summarizedIssue : "no summary";
    }

    public UUID getId() { return id; }
    public UUID getLogEntryId() { return logEntryId; }
    public LogEntry getLogEntry() { return logEntry; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSummarizedIssue() { return summarizedIssue; }
    public void setSummarizedIssue(String summarizedIssue) { this.summarizedIssue = summarizedIssue; }

    public String getLikelyCause() { return likelyCause; }
    public void setLikelyCause(String likelyCause) { this.likelyCause = likelyCause; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(double anomalyScore) {
        this.anomalyScore = Math.min(Math.max(anomalyScore, 0.0), 1.0);
    }

    public Instant getAnalyzedAt() { return analyzedAt; }

    @Override
    public String toString() {
        return "AIAnalysis{" +
                "id=" + id +
                ", logEntryId=" + logEntryId +
                ", severity=" + severity +
                ", probability=" + String.format("%.3f", anomalyScore) +
                ", category='" + category + '\'' +
                '}';
    }
}