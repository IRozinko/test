ALTER TABLE lending.loan_application ADD COLUMN source_name TEXT;
ALTER TABLE lending.loan_application_audit ADD COLUMN source_name TEXT;

ALTER TABLE lending.loan_application ADD COLUMN source_type TEXT;
ALTER TABLE lending.loan_application_audit ADD COLUMN source_type TEXT;

