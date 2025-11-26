package bbu.solution.logwatchai.domain.logsource;

import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a configured log source within the system. A log source defines
 * where logs originate (e.g., file system, remote server, API, etc.) and how
 * they should be processed. It stores parameters such as source type, file
 * path, connection metadata, polling interval, and activation status.
 * <p>
 * This entity is persisted in the {@code log_sources} table and includes
 * indexes for efficient querying by activity status and source type.
 */
@Entity
@Table(
        name = "log_sources",
        indexes = {
                @Index(name = "idx_log_sources_active", columnList = "active"),
                @Index(name = "idx_log_sources_type", columnList = "type")
        }
)
@Getter
public class LogSource {

    /**
     * Unique identifier for the log source.
     */
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    /**
     * Display name for the log source.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Type of log source (e.g., FILE, API, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogSourceType type;

    /**
     * Optional file path or location reference (used especially by FILE sources).
     */
    @Column(length = 500)
    private String path;

    /**
     * JSON-structured configuration details for dynamic or remote log sources.
     */
    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode connectionInfo;

    /**
     * Polling interval in seconds for sources that require periodic checking.
     */
    @Column(nullable = false)
    private int pollingIntervalSec = 60;

    /**
     * Indicates whether this log source is active and should be monitored.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Timestamp when the log source was created.
     * Automatically assigned by Hibernate.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Activates the log source.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivates the log source.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Updates the stored connection information JSON structure.
     *
     * @param info new connection metadata
     */
    public void updateConnectionInfo(JsonNode info) {
        this.connectionInfo = info;
    }

    // --- Explicit getters/setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
}
