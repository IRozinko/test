
ALTER TABLE iovation.transaction_detail
  DROP CONSTRAINT fk_transaction_detail_transaction_id,
  ADD CONSTRAINT fk_transaction_detail_transaction_id
    FOREIGN KEY (transaction_id) REFERENCES iovation.transaction (id) ON DELETE CASCADE;

