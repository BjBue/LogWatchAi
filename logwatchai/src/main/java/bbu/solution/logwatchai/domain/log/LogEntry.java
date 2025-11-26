package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single log entry that was ingested by the system.
 * A LogEntry stores raw log text, metadata such as timestamps and log level,
 * and optional AIAnalysis results that may indicate anomaly detection.
 *
 * <p>The entity is tightly coupled with its {@link LogSource}, defining the origin
 * of the log entry. Additionally, it may have a one-to-one relationship with
 * an {@link AIAnalysis} if the entry has been analyzed by the AI engine.</p>
 *
 * <p>Indexes are defined to optimize query performance for filtering by source,
 * ingestion time, analyzed status, anomaly flag, and log level.</p>
 */
@Entity
@Table(
        name = "log_entries",
        indexes = {
                @Index(name = "idx_log_entries_source_id", columnList = "sourceId"),
                @Index(name = "idx_log_entries_ingestion_time", columnList = "ingestionTime DESC"),
                @Index(name = "idx_log_entries_analyzed", columnList = "analyzed"),
                @Index(name = "idx_log_entries_has_anomaly", columnList = "hasAnomaly"),
                @Index(name = "idx_log_entries_level", columnList = "level")
        }
)
@DynamicUpdate
public class LogEntry {

    /**
     * Unique identifier of the log entry.
     */
    @Id
    @Column(nullable = false, columnDefinition = "BINARY(16)", updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    /**
     * Timestamp included in the original log event.
     */
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    /**
     * Timestamp of when the log entry was ingested by the system.
     */
    @Column(nullable = false, updatable = false)
    private Instant ingestionTime = Instant.now();

    /**
     * Raw log text as received from the source.
     */
    @Lob
    @Column(nullable = false, length = 65535)
    private String rawText;

    /**
     * Optional log level extracted from the log text.
     */
    @Column(length = 16)
    private String level;

    /**
     * Whether the log entry has already been processed by the AI analysis system.
     */
    @Column(nullable = false)
    private boolean analyzed = false;

    /**
     * Whether the AI system detected an anomaly in this log entry.
     */
    @Column(nullable = false)
    private boolean hasAnomaly = false;

    /**
     * Identifier of the log source this entry belongs to.
     */
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID sourceId;

    /**
     * Reference to the log source entity.
     * This field is read-only and derived from {@code sourceId}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceId", insertable = false, updatable = false)
    private LogSource source;

    /**
     * AI analysis associated with this log entry.
     * Loaded lazily to avoid unnecessary cost.
     */
    @OneToOne(mappedBy = "logEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AIAnalysis analysis;

    /**
     * Default JPA constructor.
     */
    public LogEntry() {}

    /**
     * Creates a new log entry from raw log text and source identifier.
     *
     * @param rawText  the raw log message
     * @param sourceId the UUID of the log source
     */
    public LogEntry(String rawText, UUID sourceId) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.ingestionTime = Instant.now();
        this.rawText = rawText;
        this.sourceId = sourceId;
        this.analyzed = false;
        this.hasAnomaly = false;
    }

    /**
     * Marks the entry as analyzed and attaches an AI analysis result.
     *
     * @param analysis AI analysis instance
     */
    public void markAsAnalyzed(AIAnalysis analysis) {
        this.analyzed = true;
        this.analysis = analysis;
        this.hasAnomaly = analysis != null && analysis.getAnomalyScore() > 0.7;
    }

    /**
     * Marks the entry as analyzed without attaching an AI analysis.
     */
    public void markAsAnalyzed() {
        this.analyzed = true;
    }

    /**
     * Sets the anomaly detection flag.
     *
     * @param anomaly whether an anomaly was detected
     */
    public void detectAnomaly(boolean anomaly) {
        this.hasAnomaly = anomaly;
    }

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public Instant getIngestionTime() { return ingestionTime; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public boolean isAnalyzed() { return analyzed; }
    public boolean hasAnomaly() { return hasAnomaly; }

    public UUID getSourceId() { return sourceId; }
    public AIAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(AIAnalysis analysis) { this.analysis = analysis; }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", sourceId=" + sourceId +
                ", analyzed=" + analyzed +
                ", hasAnomaly=" + hasAnomaly +
                '}';
    }
}
