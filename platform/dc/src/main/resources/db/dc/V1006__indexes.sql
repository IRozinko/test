-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_debt_portfolio ON dc.debt USING btree (portfolio);

-- NEW INDEXES
CREATE INDEX IF NOT EXISTS idx_debt_agent ON dc.debt USING btree (agent);
CREATE INDEX IF NOT EXISTS idx_debt_next_action_at ON dc.debt USING btree (next_action_at);
CREATE INDEX IF NOT EXISTS idx_debt_status ON dc.debt USING btree (status);
CREATE INDEX IF NOT EXISTS idx_debt_loan_number_trgm ON dc.debt USING gin (loan_number gin_trgm_ops);
