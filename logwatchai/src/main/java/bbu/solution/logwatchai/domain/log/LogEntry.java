package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

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
@DynamicUpdate  // nur geänderte Felder updaten → Performance
public class LogEntry {

    @Id
    @Column(nullable = false, columnDefinition = "BINARY(16)", updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private Instant ingestionTime = Instant.now();

    @Lob
    @Column(nullable = false, length = 65535)  // TEXT in MariaDB
    private String rawText;

    // Optional: Log-Level aus Parsing (INFO, ERROR, WARN, DEBUG...)
    @Column(length = 16)
    private String level;

    // Optimistische Sperre für parallele Analysen
    //@Version
    //private Long version;

    // Status-Flags
    @Column(nullable = false)
    private boolean analyzed = false;

    @Column(nullable = false)
    private boolean hasAnomaly = false;

    // Fremdschlüssel zur Quelle
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID sourceId;

    // 1:1 Beziehung zur KI-Analyse (lazy, damit nicht immer geladen)
    @OneToOne(mappedBy = "logEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AIAnalysis analysis;

    // ===================================================================
    // Konstruktoren
    // ===================================================================

    public LogEntry() {}

    public LogEntry(String rawText, UUID sourceId) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.ingestionTime = Instant.now();
        this.rawText = rawText;
        this.sourceId = sourceId;
        this.analyzed = false;
        this.hasAnomaly = false;
    }

    // ===================================================================
    // Business-Methoden
    // ===================================================================

    public void markAsAnalyzed(AIAnalysis analysis) {
        this.analyzed = true;
        this.analysis = analysis;
        this.hasAnomaly = analysis != null && analysis.getAnomalyScore() > 0.7; // Beispiel-Threshold
    }

    public void markAsAnalyzed() {
        this.analyzed = true;
    }

    public void detectAnomaly(boolean anomaly) {
        this.hasAnomaly = anomaly;
    }

    // ===================================================================
    // Getter & Setter
    // ===================================================================

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