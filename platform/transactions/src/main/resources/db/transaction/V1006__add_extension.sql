ALTER TABLE transaction.transaction
  ADD COLUMN extension INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN extension_days INTEGER NOT NULL DEFAULT 0;


ALTER TABLE transaction.transaction_audit
  ADD COLUMN extension INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN extension_days INTEGER NOT NULL DEFAULT 0;
