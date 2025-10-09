-- V18__Alter_Payment_Reason_To_Text.sql
-- Aumenta el tamaño de la columna 'reason' en la tabla 'payment_transactions'
-- para poder almacenar mensajes de error largos de las pasarelas de pago.

ALTER TABLE payment_transactions
MODIFY COLUMN reason TEXT NULL;