-- V9__init_inventory.sql
-- Tabla de inventario por producto y tenant.
-- Regla: available NULL => stock ilimitado. reserved >= 0.
-- Invariantes con CHECKs (MySQL 8.x los aplica).

CREATE TABLE IF NOT EXISTS inventory (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  available INT NULL,                    -- NULL = ilimitado; si no es NULL, >= 0
  reserved INT NOT NULL DEFAULT 0,       -- >= 0
  version INT NOT NULL DEFAULT 0,        -- @Version (optimistic locking)
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT pk_inventory PRIMARY KEY (id),

  -- Unicidad por tenant + producto
  CONSTRAINT uq_inventory_tenant_product UNIQUE (tenant_id, product_id),

  -- FKs (consistencia básica)
  CONSTRAINT fk_inventory_tenant
    FOREIGN KEY (tenant_id) REFERENCES restaurants(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_inventory_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,

  -- Invariantes
  CONSTRAINT chk_inventory_available_nonneg
    CHECK (available IS NULL OR available >= 0),
  CONSTRAINT chk_inventory_reserved_nonneg
    CHECK (reserved >= 0),
  CONSTRAINT chk_inventory_reserved_le_available
    CHECK (available IS NULL OR reserved <= available)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Índices para consultas típicas (listado por tenant y joins con product)
CREATE INDEX idx_inventory_tenant ON inventory (tenant_id);
CREATE INDEX idx_inventory_product ON inventory (product_id);
-- Útil si filtras por disponibilidad dentro de un tenant (admin list)
CREATE INDEX idx_inventory_tenant_available ON inventory (tenant_id, available);
