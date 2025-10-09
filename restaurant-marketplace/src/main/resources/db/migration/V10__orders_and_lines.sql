-- V10__orders_and_lines.sql
-- Pedidos y sus líneas

-- 1) orders: 1 pedido = 1 restaurante (tenant) y 1 cliente
CREATE TABLE IF NOT EXISTS orders (
  id                BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id         BIGINT NOT NULL,
  customer_id       BIGINT NOT NULL,
  status            VARCHAR(20)     NOT NULL, -- CREATED | PAID | CANCELLED
  total_amount      DECIMAL(19,2)   NOT NULL,
  currency          CHAR(3)         NOT NULL, -- ISO-4217
  created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  -- Búsquedas admin por tenant y estado/fecha
  KEY idx_orders_tenant_status_created (tenant_id, status, created_at),
  -- Historial de un cliente
  KEY idx_orders_customer_created (customer_id, created_at),

  CONSTRAINT fk_orders_restaurant
    FOREIGN KEY (tenant_id) REFERENCES restaurants(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_orders_customer
    FOREIGN KEY (customer_id) REFERENCES users(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) order_lines: snapshot de nombre y precio al momento del pedido
CREATE TABLE IF NOT EXISTS order_lines (
  id                   BIGINT NOT NULL AUTO_INCREMENT,
  order_id             BIGINT NOT NULL,
  product_id           BIGINT NOT NULL,
  product_name         VARCHAR(255)    NOT NULL,
  unit_price_amount    DECIMAL(19,2)   NOT NULL,
  unit_price_currency  CHAR(3)         NOT NULL,
  qty                  INT             NOT NULL,
  line_total_amount    DECIMAL(19,2)   NOT NULL,

  PRIMARY KEY (id),

  -- Para cargar líneas por pedido eficientemente
  KEY idx_order_lines_order (order_id),
  -- Para reporting básico por producto
  KEY idx_order_lines_product (product_id),

  CONSTRAINT fk_order_lines_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_order_lines_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
