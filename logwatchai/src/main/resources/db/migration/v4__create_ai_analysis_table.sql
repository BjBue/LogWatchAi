CREATE TABLE IF NOT EXISTS ai_analyses (
    id                  BINARY(16)   NOT NULL PRIMARY KEY,
    log_entry_id        BINARY(16)   NOT NULL,
    severity            ENUM('INFO','LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    category            VARCHAR(100),
    summarized_issue    TEXT,
    likely_cause        TEXT,
    recommendation      TEXT,
    anomaly_score       DOUBLE       NOT NULL DEFAULT 0.0
    CHECK (anomaly_score BETWEEN 0.0 AND 1.0),
    analyzed_at         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_ai_analyses_log_entry
    FOREIGN KEY (log_entry_id) REFERENCES log_entries(id)
    ON DELETE CASCADE,

    INDEX idx_ai_analyses_log_entry_id (log_entry_id),
    INDEX idx_ai_analyses_severity (severity),
    INDEX idx_ai_analyses_anomaly_score (anomaly_score DESC),
    INDEX idx_ai_analyses_analyzed_at (analyzed_at DESC),
    INDEX idx_ai_analyses_combo (severity, anomaly_score)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;