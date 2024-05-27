
ALTER TABLE transaction.transaction_entry
  DROP CONSTRAINT fk_transaction_entry_transaction_id,
  ADD CONSTRAINT fk_transaction_entry_transaction_id FOREIGN KEY (transaction_id)
    REFERENCES transaction.transaction (id) ON DELETE CASCADE;

ALTER TABLE transaction.transaction 
  DROP CONSTRAINT fk_transaction_voids_transaction_id,
  ADD CONSTRAINT fk_transaction_voids_transaction_id 
    FOREIGN KEY (voids_transaction_id) REFERENCES transaction.transaction(id) ON DELETE CASCADE;
