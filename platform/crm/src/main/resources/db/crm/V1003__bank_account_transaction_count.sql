ALTER TABLE crm.client_bank_account
  ADD COLUMN number_of_transactions BIGINT NOT NULL DEFAULT 0;
ALTER TABLE crm.client_bank_account_audit
  ADD COLUMN number_of_transactions BIGINT;
