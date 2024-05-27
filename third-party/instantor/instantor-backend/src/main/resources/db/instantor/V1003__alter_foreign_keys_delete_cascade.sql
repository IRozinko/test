
ALTER TABLE instantor.transaction 
  DROP CONSTRAINT fk_transaction_response_id,
  ADD CONSTRAINT fk_transaction_response_id 
    FOREIGN KEY (response_id) REFERENCES instantor.response (id) ON DELETE CASCADE;
