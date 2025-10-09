-- V1: Base para módulo Restaurant (tenant)
-- MySQL 8.x + Flyway

CREATE TABLE restaurants (
  id              BIGINT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(120)  NOT NULL,
  slug            VARCHAR(140)  NOT NULL,             -- para URLs únicas por restaurant
  email           VARCHAR(255)  NULL,
  phone           VARCHAR(30)   NULL,

  -- Dirección (simple, puedes ampliar luego)
  address_line1   VARCHAR(255)  NULL,
  address_line2   VARCHAR(255)  NULL,
  city            VARCHAR(120)  NULL,
  country         VARCHAR(2)    NULL,                 -- ISO-3166-1 alpha-2 (ej. "US", "ES")
  postal_code     VARCHAR(20)   NULL,

  -- Estado operativo
  status          VARCHAR(12)   NOT NULL DEFAULT 'CLOSED',  -- OPEN | CLOSED | SUSPENDED

  -- Horarios en JSON (flexible)
  opening_hours   JSON          NULL,

  created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  CONSTRAINT uq_restaurants_slug   UNIQUE (slug),
  CONSTRAINT uq_restaurants_email  UNIQUE (email),
  CONSTRAINT chk_restaurants_status CHECK (status IN ('OPEN','CLOSED','SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices útiles
CREATE INDEX idx_restaurants_status ON restaurants (status);
CREATE INDEX idx_restaurants_city   ON restaurants (city);
