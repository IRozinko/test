CREATE INDEX IF NOT EXISTS idx_transaction_account_id ON instantor.transaction USING btree (account_id);
