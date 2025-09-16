CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(32) NOT NULL,              -- RESTAURANT_ADMIN | SUPER_ADMIN
  tenant_id BIGINT NULL,                  -- NULL for SUPER_ADMIN; FK to restaurants.id (optional at DB level for now)
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_tenant ON users(tenant_id);

-- Optional seed for local dev (use real BCrypt hashes):
-- password = "123456"
INSERT INTO users (email, password_hash, role, tenant_id) VALUES
('admin@resto.example', '$2a$10$8uF57oOnSQefZd1jrko5YeGxbXev4MZbhjKcmgwZyTY9cFMd8Q3eS', 'RESTAURANT_ADMIN', 1),
('super@platform.example', '$2a$10$8uF57oOnSQefZd1jrko5YeGxbXev4MZbhjKcmgwZyTY9cFMd8Q3eS', 'SUPER_ADMIN', NULL);