-- V11__alter_refresh_tokens_token_hash_to_varchar.sql
ALTER TABLE refresh_tokens
  MODIFY token_hash VARCHAR(64) NOT NULL;
