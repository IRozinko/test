-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_transaction_installment_id ON transaction.transaction USING btree (installment_id);

-- NEW INDEXES
CREATE INDEX IF NOT EXISTS idx_transaction_unidentified_liabilities_to_customers_sub_type ON transaction.transaction USING btree (voided, transaction_sub_type)
  WHERE transaction_sub_type = 'UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS';
CREATE INDEX IF NOT EXISTS idx_transaction_created_at on transaction.transaction using btree (created_at);
