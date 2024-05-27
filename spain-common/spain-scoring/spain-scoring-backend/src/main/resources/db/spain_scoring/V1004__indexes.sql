-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_spain_scoring_log_created_at ON spain_scoring.log USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_log_application_id ON spain_scoring.log USING btree (application_id);
CREATE INDEX IF NOT EXISTS idx_log_loan_id ON spain_scoring.log USING btree (loan_id);
