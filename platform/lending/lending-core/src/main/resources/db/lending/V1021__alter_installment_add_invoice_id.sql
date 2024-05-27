
ALTER TABLE lending.installment ADD COLUMN invoice_id BIGINT;
ALTER TABLE lending.installment_audit ADD COLUMN invoice_id BIGINT;
