-- OLD INDEXES

-- Improve performance of invoicing scheduler
CREATE INDEX IF NOT EXISTS idx_invoice_generate_file ON lending.invoice (generate_file)
  WHERE generate_file = true;
CREATE INDEX IF NOT EXISTS idx_invoice_send_file ON lending.invoice (send_file)
  WHERE send_file = true;

CREATE INDEX IF NOT EXISTS idx_loan_application_created_at ON lending.loan_application (created_at);
CREATE INDEX IF NOT EXISTS idx_loan_created_at ON lending.loan (created_at);
CREATE INDEX IF NOT EXISTS idx_loan_loans_paid ON lending.loan (loans_paid);
CREATE INDEX IF NOT EXISTS idx_loan_snapshot_effective_from ON lending.loan_daily_snapshot (effective_from);
CREATE INDEX IF NOT EXISTS idx_loan_snapshot_loans_paid ON lending.loan_daily_snapshot (loans_paid);
CREATE INDEX IF NOT EXISTS idx_loan_snapshot_overdue_days ON lending.loan_daily_snapshot (overdue_days);
CREATE INDEX IF NOT EXISTS idx_loan_application_workflow_id ON lending.loan_application USING btree (workflow_id);

-- NEW INDEXES
CREATE INDEX IF NOT EXISTS idx_loan_status_detail ON lending.loan USING btree (status_detail);
CREATE INDEX IF NOT EXISTS idx_loan_loan_number_trgm ON lending.loan USING gin (loan_number gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_loan_issue_date ON lending.loan USING btree (issue_date);

CREATE INDEX IF NOT EXISTS idx_loan_application_application_number ON lending.loan_application USING gin (application_number gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_loan_application_type ON lending.loan_application USING btree (type);
CREATE INDEX IF NOT EXISTS idx_loan_application_source_name ON lending.loan_application USING btree (source_name);

CREATE INDEX IF NOT EXISTS idx_discount_client_id ON lending.discount USING btree (client_id);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS lending.idx_loan_application_attribute_client_id;
