ALTER TABLE refresh_tokens
  ADD COLUMN subject_type VARCHAR(16) NOT NULL DEFAULT 'ADMIN',
  ADD INDEX idx_refresh_user_type_revoked (user_id, subject_type, revoked);
