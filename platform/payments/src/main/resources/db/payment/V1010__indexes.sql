-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_disbursement_created_at ON payment.disbursement (created_at);

-- Improve pending disbursement check performance
CREATE INDEX IF NOT EXISTS idx_disbursement_status_detail_pending ON payment.disbursement (status_detail)
  WHERE status_detail = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_disbursement_value_date ON payment.disbursement (value_date);
CREATE INDEX IF NOT EXISTS idx_payment_created_at ON payment.payment (created_at);

-- NEW INDEXES
CREATE INDEX IF NOT EXISTS idx_payment_details_trgm ON payment.payment USING gin (details gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_payment_reference_trgm ON payment.payment USING gin (reference gin_trgm_ops);

DROP INDEX IF EXISTS payment.idx_payment_status_detail_pending;
CREATE INDEX IF NOT EXISTS idx_payment_status_details ON payment.payment USING btree (status_detail);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS payment.idx_statement_row_attributes_statement_row_id;
