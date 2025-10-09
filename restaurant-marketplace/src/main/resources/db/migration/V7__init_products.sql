-- V7__init_products.sql
-- Products catalog per restaurant (tenant).

CREATE TABLE products (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id       BIGINT NOT NULL,                       -- FK -> restaurants.id
    sku             VARCHAR(64) NOT NULL,                  -- unique per tenant
    name            VARCHAR(255) NOT NULL,
    description     TEXT NULL,
    price_amount    DECIMAL(19,2) NOT NULL,                -- Money amount
    price_currency  CHAR(3) NOT NULL,                      -- ISO-4217
    category        VARCHAR(100) NOT NULL DEFAULT 'uncategorized',
    published       TINYINT(1) NOT NULL DEFAULT 0,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,

    -- Primary Key
    CONSTRAINT pk_products PRIMARY KEY (id),

    -- Foreign Key
    CONSTRAINT fk_products_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES restaurants (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,

    -- Unique Constraints
    CONSTRAINT ux_products_tenant_sku UNIQUE (tenant_id, sku)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
 
-- Indexes to optimize queries
CREATE INDEX ix_products_tenant_published
    ON products (tenant_id, published);

CREATE INDEX ix_products_tenant_category
    ON products (tenant_id, category);

CREATE INDEX ix_products_tenant_created
    ON products (tenant_id, created_at);
