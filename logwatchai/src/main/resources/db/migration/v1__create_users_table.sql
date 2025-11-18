CREATE TABLE IF NOT EXISTS users (
    id              BINARY(16)   NOT NULL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    role            ENUM('USER','ADMIN','ANALYST','AUDITOR') NOT NULL DEFAULT 'USER',
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    last_login      DATETIME(6)  NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_users_username UNIQUE (username),
    INDEX idx_users_created_at (created_at),
    INDEX idx_users_role (role)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;