-- V10__add_version_to_refresh_tokens.sql
ALTER TABLE refresh_tokens
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0;