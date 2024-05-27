-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_nordigen_log_created_at ON nordigen.log USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_log_application_id ON nordigen.log USING btree (application_id);
CREATE INDEX IF NOT EXISTS idx_log_instantor_response_id ON nordigen.log USING btree (instantor_response_id);
CREATE INDEX IF NOT EXISTS idx_log_loan_id ON nordigen.log USING btree (loan_id);
