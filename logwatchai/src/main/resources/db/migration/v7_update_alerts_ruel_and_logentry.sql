ALTER TABLE alerts
DROP COLUMN rule_name;

ALTER TABLE alerts
    ADD COLUMN rule_names JSON NOT NULL DEFAULT '[]';

ALTER TABLE alerts
    ADD COLUMN log_entry_id BINARY(16) NULL AFTER source_id;

CREATE INDEX IF NOT EXISTS idx_alerts_log_entry_id ON alerts (log_entry_id);

ALTER TABLE log_entries
    ADD CONSTRAINT uq_log_entries_source_raw UNIQUE (source_id, raw_text(200));