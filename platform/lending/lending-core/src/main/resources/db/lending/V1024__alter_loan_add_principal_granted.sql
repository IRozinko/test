ALTER TABLE lending.loan
  ADD COLUMN principal_granted NUMERIC(19, 4) NOT NULL DEFAULT 0;

UPDATE lending.loan SET principal_granted = principal_disbursed;

ALTER TABLE lending.loan_audit
  ADD COLUMN principal_granted NUMERIC(19, 4);

UPDATE lending.loan_audit SET principal_granted = principal_disbursed;
