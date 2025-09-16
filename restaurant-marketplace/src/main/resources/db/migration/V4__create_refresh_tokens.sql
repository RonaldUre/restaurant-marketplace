-- V9__create_refresh_tokens.sql
CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    jti VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    revoked TINYINT(1) NOT NULL DEFAULT 0,
    replaced_by_jti VARCHAR(36) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_ip VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY ux_refresh_tokens_jti (jti),
    KEY ix_refresh_tokens_user (user_id),
    KEY ix_refresh_tokens_expires (expires_at),
    KEY ix_refresh_tokens_revoked_created (revoked, created_at),
    KEY ix_refresh_tokens_user_revoked (user_id, revoked),

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;