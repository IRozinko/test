ALTER TABLE lending.loan_application ADD COLUMN discount_id BIGINT;
ALTER TABLE lending.loan_application_audit ADD COLUMN discount_id BIGINT;

ALTER TABLE lending.loan ADD COLUMN discount_id BIGINT;
ALTER TABLE lending.loan_audit ADD COLUMN discount_id BIGINT;
