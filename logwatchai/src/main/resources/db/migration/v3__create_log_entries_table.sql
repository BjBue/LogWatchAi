-- V3__create_log_entries_table.sql
CREATE TABLE IF NOT EXISTS log_entries (
    id              BINARY(16)   NOT NULL PRIMARY KEY,
    timestamp       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ingestion_time  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    raw_text        TEXT         NOT NULL,
    analyzed        BOOLEAN      NOT NULL DEFAULT FALSE,
    has_anomaly     BOOLEAN      NOT NULL DEFAULT FALSE,
    level           VARCHAR(16),
--    version         BIGINT       NOT NULL DEFAULT 0,
    source_id       BINARY(16)   NOT NULL,

    CONSTRAINT fk_log_entries_source
       FOREIGN KEY (source_id) REFERENCES log_sources(id)
           ON DELETE RESTRICT,

    INDEX idx_log_entries_source_id (source_id),
    INDEX idx_log_entries_ingestion_time (ingestion_time DESC),
    INDEX idx_log_entries_analyzed (analyzed),
    INDEX idx_log_entries_has_anomaly (has_anomaly),
    INDEX idx_log_entries_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;