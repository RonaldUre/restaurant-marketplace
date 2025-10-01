-- Clientes finales (no multi-tenant)

CREATE TABLE IF NOT EXISTS customers (
  id             BIGINT NOT NULL AUTO_INCREMENT,
  email          VARCHAR(255)  NOT NULL,
  name           VARCHAR(120)  NOT NULL,
  phone          VARCHAR(30)   NULL,
  password_hash  VARCHAR(255)  NOT NULL,
  created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uk_customers_email (email),
  UNIQUE KEY uk_customers_phone (phone),
  KEY idx_customers_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;