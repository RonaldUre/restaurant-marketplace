-- V12__idempotency_keys.sql
-- Llaves de idempotencia para POST /orders

CREATE TABLE IF NOT EXISTS idempotency_keys (
  id                BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id         BIGINT NOT NULL,
  customer_id       BIGINT NOT NULL,
  idempotency_key   VARCHAR(100)    NOT NULL,   -- valor enviado por el cliente
  order_id          BIGINT NOT NULL,   -- resultado previamente generado
  created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  -- Evita procesar dos veces el mismo intento para el mismo cliente y tenant
  UNIQUE KEY uk_idem_tenant_customer_key (tenant_id, customer_id, idempotency_key),

  -- Limpiezas/consultas por fecha
  KEY idx_idem_created (created_at),

  CONSTRAINT fk_idem_tenant
    FOREIGN KEY (tenant_id) REFERENCES restaurants(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_idem_customer
    FOREIGN KEY (customer_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_idem_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
