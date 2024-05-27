-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_iovation_tranasction_created_at ON iovation.transaction USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_blackbox_client_id ON iovation.blackbox USING btree (client_id);
CREATE INDEX IF NOT EXISTS idx_transaction_application_id ON iovation.transaction USING btree (application_id);
CREATE INDEX IF NOT EXISTS idx_transaction_client_id ON iovation.transaction USING btree (client_id);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS iovation.idx_transaction_detail_transaction_id;
