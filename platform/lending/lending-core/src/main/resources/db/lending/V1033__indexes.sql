CREATE INDEX IF NOT EXISTS idx_loan_status ON lending.loan USING btree (status);
