ALTER TABLE lending.loan
  ADD COLUMN early_repayment_received NUMERIC(19, 4) NOT NULL DEFAULT 0.0000;

ALTER TABLE lending.loan_audit
  ADD COLUMN early_repayment_received NUMERIC(19, 4);
