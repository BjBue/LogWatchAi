package bbu.solution.logwatchai.domain.alert;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts",
        indexes = {
                @Index(name = "idx_alerts_active", columnList = "active"),
                @Index(name = "idx_alerts_severity", columnList = "severity"),
                @Index(name = "idx_alerts_created_at", columnList = "created_at DESC"),
                @Index(name = "idx_alerts_source_id", columnList = "source_id")
        })
public class Alert {

    @Id @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "source_id", columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID sourceId;

    public enum Severity { INFO, WARNING, CRITICAL }

    public void activate()   { this.active = true; }
    public void deactivate() { this.active = false; }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public boolean isActive() { return active; }
    public UUID getSourceId() { return sourceId; }
    public void setSourceId(UUID sourceId) { this.sourceId = sourceId; }
}