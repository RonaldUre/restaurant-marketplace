ALTER TABLE order_lines
  MODIFY unit_price_currency varchar(3) NOT NULL;

ALTER TABLE orders
  MODIFY currency varchar(3) NOT NULL;
