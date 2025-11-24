CREATE TABLE IF NOT EXISTS alerts (
    id              BINARY(16) NOT NULL PRIMARY KEY,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    severity        ENUM('INFO','WARNING','CRITICAL') NOT NULL,
    message         VARCHAR(500) NOT NULL,
    rule_names      JSON NOT NULL DEFAULT '[]',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    source_id       BINARY(16) NULL,
    log_entry_id    BINARY(16) NULL,

    CONSTRAINT fk_alerts_source
    FOREIGN KEY (source_id) REFERENCES log_sources(id)
    ON DELETE SET NULL,

    INDEX idx_alerts_active (active),
    INDEX idx_alerts_severity (severity),
    INDEX idx_alerts_created_at (created_at DESC),
    INDEX idx_alerts_source_id (source_id),
    INDEX idx_alerts_log_entry_id (log_entry_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;