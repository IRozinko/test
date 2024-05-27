ALTER TABLE transaction.transaction
  ADD COLUMN dpd INTEGER;

ALTER TABLE transaction.transaction_audit
  ADD COLUMN dpd INTEGER;
