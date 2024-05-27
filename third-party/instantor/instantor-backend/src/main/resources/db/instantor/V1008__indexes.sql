-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_instantor_response_created_at ON instantor.response USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_transaction_client_id ON instantor.transaction USING btree (client_id);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS instantor.idx_response_attribute_response_id;
