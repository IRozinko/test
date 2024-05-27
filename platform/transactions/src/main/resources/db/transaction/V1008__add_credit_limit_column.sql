ALTER TABLE transaction.transaction
  ADD COLUMN credit_limit NUMERIC(19, 4) NOT NULL DEFAULT 0.0000;

ALTER TABLE transaction.transaction_audit
  ADD COLUMN credit_limit NUMERIC(19, 4) NOT NULL DEFAULT 0.0000;
