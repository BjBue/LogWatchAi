ALTER TABLE alerts
DROP COLUMN rule_name;

ALTER TABLE alerts
    ADD COLUMN rule_names JSON NOT NULL DEFAULT '[]';