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
-- password = "admin123"
INSERT INTO users (email, password_hash, role, tenant_id) VALUES
('admin@resto.example', '$2a$10$w7pg9YV3wJrK0f5aC0o8E.2C5H6h0q3y5mD7O2z0i2r3v8pQy1jD6', 'RESTAURANT_ADMIN', 1),
('super@platform.example', '$2a$10$w7pg9YV3wJrK0f5aC0o8E.2C5H6h0q3y5mD7O2z0i2r3v8pQy1jD6', 'SUPER_ADMIN', NULL);