package bbu.solution.logwatchai.domain.log;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "log_entries")
public class LogEntry {

    @Id
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false)
    private Instant timestamp;

    @Lob
    @Column(nullable = false)
    private String rawText;

    @Column(nullable = false)
    private Instant ingestionTime;

    @Column(nullable = false)
    private boolean analyzed;

    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID sourceId;

    public LogEntry() {
        // JPA requires empty constructor
    }

    public LogEntry(String rawText, UUID sourceId) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.ingestionTime = Instant.now();
        this.rawText = rawText;
        this.analyzed = false;
        this.sourceId = sourceId;
    }

    public void markAnalyzed() {
        this.analyzed = true;
    }

    // ---- getters + setters ----

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getRawText() { return rawText; }
    public Instant getIngestionTime() { return ingestionTime; }
    public boolean isAnalyzed() { return analyzed; }
    public UUID getSourceId() { return sourceId; }

    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }
}
