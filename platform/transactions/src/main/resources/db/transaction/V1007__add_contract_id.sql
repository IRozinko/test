ALTER TABLE transaction.transaction
  ADD COLUMN contract_id BIGINT;


ALTER TABLE transaction.transaction_audit
  ADD COLUMN contract_id BIGINT;
