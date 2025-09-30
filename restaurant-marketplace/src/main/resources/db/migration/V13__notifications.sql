-- V13__notifications.sql
-- Logs de notificaciones (envíos de email de Ordering)
-- Tipos esperados: ORDER_CONFIRMED | ORDER_CANCELLED | PAYMENT_FAILED
-- Estados: PENDING | SENT | FAILED

CREATE TABLE IF NOT EXISTS notification_logs (
  id               BIGINT NOT NULL AUTO_INCREMENT,

  -- Claves de negocio
  tenant_id        BIGINT       NOT NULL,      -- restaurante (tenant)
  order_id         BIGINT       NOT NULL,      -- pedido asociado

  -- Metadata del mensaje
  type             VARCHAR(30)  NOT NULL,      -- ORDER_CONFIRMED | ORDER_CANCELLED | PAYMENT_FAILED
  status           VARCHAR(20)  NOT NULL,      -- PENDING | SENT | FAILED
  attempts         INT          NOT NULL DEFAULT 0,
  to_email         VARCHAR(255)     NULL,      -- puede faltar (ej. cliente sin email) => FAILED
  subject          VARCHAR(255) NOT NULL,
  body             TEXT         NOT NULL,
  last_error       VARCHAR(500)     NULL,

  -- Timestamps
  created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_attempt_at  TIMESTAMP        NULL DEFAULT NULL,
  sent_at          TIMESTAMP        NULL DEFAULT NULL,

  PRIMARY KEY (id),

  -- Búsquedas por tenant/estado/fecha en backoffice
  KEY idx_notifications_tenant_created (tenant_id, created_at),
  KEY idx_notifications_tenant_status_created (tenant_id, status, created_at),

  -- Apoyo para auditoría por pedido
  KEY idx_notifications_order (order_id),

  -- Filtros secundarios
  KEY idx_notifications_type (type),

  CONSTRAINT fk_notifications_restaurant
    FOREIGN KEY (tenant_id) REFERENCES restaurants(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_notifications_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
