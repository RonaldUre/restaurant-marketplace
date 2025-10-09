-- V11__payments.sql
-- Transacciones de pago (adapter fake ahora, Stripe/PayPal luego)

CREATE TABLE IF NOT EXISTS payment_transactions (
  id              BIGINT NOT NULL AUTO_INCREMENT,
  order_id        BIGINT NOT NULL,
  tenant_id       BIGINT NOT NULL,
  amount          DECIMAL(19,2)   NOT NULL,
  currency        CHAR(3)         NOT NULL,
  method          VARCHAR(50)     NOT NULL,    -- e.g., CARD, CASH, FAKE
  status          VARCHAR(20)     NOT NULL,    -- INITIATED | APPROVED | DECLINED
  tx_id           VARCHAR(100)    NULL,        -- id de pasarela si aplica
  reason          VARCHAR(255)    NULL,        -- motivo de rechazo u observaciones
  created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  -- Un pago por pedido (en MVP). Si más adelante permites reintentos, migras esta constraint.
  UNIQUE KEY uk_payments_order (order_id),

  -- Búsquedas por tx_id desde webhooks/pasarela
  KEY idx_payments_txid (tx_id),
  -- Consultas por tenant/fecha
  KEY idx_payments_tenant_created (tenant_id, created_at),

  CONSTRAINT fk_payments_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_payments_restaurant
    FOREIGN KEY (tenant_id) REFERENCES restaurants(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
