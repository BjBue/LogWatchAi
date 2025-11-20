package bbu.solution.logwatchai.domain.alert;

import bbu.solution.logwatchai.domain.analysis.Severity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "alerts",
        indexes = {
                @Index(name = "idx_alerts_active", columnList = "active"),
                @Index(name = "idx_alerts_severity", columnList = "severity"),
                @Index(name = "idx_alerts_created_at", columnList = "created_at DESC"),
                @Index(name = "idx_alerts_source_id", columnList = "source_id")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private Severity severity;

    @Column(nullable = false, length = 500)
    @Setter
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rule_names", columnDefinition = "json", nullable = false)
    private List<String> ruleNames = new ArrayList<>();

    @Column(nullable = false)
    @Setter
    private boolean active = true;

    @Column(name = "source_id", columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    @Setter
    private UUID sourceId;

    // --- Builder for easier creation ---
    @Builder
    public Alert(Severity severity, String message, List<String> ruleNames, UUID sourceId) {
        this.severity = severity;
        this.message = message;
        this.sourceId = sourceId;
        this.ruleNames = ruleNames == null ? new ArrayList<>() : new ArrayList<>(ruleNames);
    }

    // --- Domain methods ---
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void setRuleNames(List<String> ruleNames) {
        this.ruleNames = (ruleNames == null)
                ? new ArrayList<>()
                : new ArrayList<>(ruleNames);
    }
}
