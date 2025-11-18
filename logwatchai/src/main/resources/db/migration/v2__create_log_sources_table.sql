CREATE TABLE IF NOT EXISTS log_sources (
    id                    BINARY(16)   NOT NULL PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    type                  VARCHAR(30)  NOT NULL,
    path                  VARCHAR(500),
    connection_info       JSON         NULL,
    polling_interval_sec  INT          NOT NULL DEFAULT 60 CHECK (polling_interval_sec > 0),
    active                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    INDEX idx_log_sources_active (active),
    INDEX idx_log_sources_type (type),
    INDEX idx_log_sources_name (name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;