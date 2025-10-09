-- V8__price_currency_to_varchar.sql
ALTER TABLE products
MODIFY price_currency VARCHAR(3) NOT NULL;
