CREATE TABLE IF NOT EXISTS log_entries (
    id BINARY(16) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    raw_text TEXT NOT NULL,
    ingestion_time TIMESTAMP NOT NULL,
    analyzed BOOLEAN NOT NULL,
    source_id BINARY(16),
    PRIMARY KEY (id)
) ENGINE=InnoDB;