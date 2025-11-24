CREATE TABLE IF NOT EXISTS daily_reports (
    id              BINARY(16)   NOT NULL PRIMARY KEY,
    reported_date   DATE         NULL,
    generated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    content         LONGTEXT     NOT NULL,
    top_issues      LONGTEXT     NULL CHECK (JSON_VALID(top_issues)),
    delivered       BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_daily_reports_date UNIQUE (reported_date),
    INDEX idx_daily_reports_delivered (delivered)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;