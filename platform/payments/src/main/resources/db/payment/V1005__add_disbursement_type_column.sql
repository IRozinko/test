ALTER TABLE payment.disbursement ADD COLUMN disbursement_type TEXT NOT NULL DEFAULT 'PRINCIPAL';
ALTER TABLE payment.disbursement_audit ADD COLUMN disbursement_type TEXT;
