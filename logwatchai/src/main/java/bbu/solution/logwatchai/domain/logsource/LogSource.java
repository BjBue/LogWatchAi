package bbu.solution.logwatchai.domain.logsource;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "log_sources",
        indexes = {
                @Index(name = "idx_log_sources_active", columnList = "active"),
                @Index(name = "idx_log_sources_type", columnList = "type")
        })
public class LogSource {

    @Id @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogSourceType type;

    @Column(length = 500)
    private String path;

    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode connectionInfo;

    @Column(nullable = false)
    private int pollingIntervalSec = 60;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public void activate()   { this.active = true; }
    public void deactivate() { this.active = false; }

    public void updateConnectionInfo(JsonNode info) {
        this.connectionInfo = info;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LogSourceType getType() { return type; }
    public void setType(LogSourceType type) { this.type = type; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public JsonNode getConnectionInfo() { return connectionInfo; }
    public void setConnectionInfo(JsonNode connectionInfo) { this.connectionInfo = connectionInfo; }
    public int getPollingIntervalSec() { return pollingIntervalSec; }
    public void setPollingIntervalSec(int pollingIntervalSec) { this.pollingIntervalSec = pollingIntervalSec; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}