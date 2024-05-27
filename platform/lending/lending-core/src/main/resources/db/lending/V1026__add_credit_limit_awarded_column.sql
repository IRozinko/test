ALTER TABLE lending.loan
  ADD COLUMN credit_limit_awarded NUMERIC(19, 4) NOT NULL DEFAULT 0.0000;

ALTER TABLE lending.loan_audit
  ADD COLUMN credit_limit_awarded NUMERIC(19, 4);
