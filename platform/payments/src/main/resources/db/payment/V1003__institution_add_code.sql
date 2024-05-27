ALTER TABLE payment.institution ADD COLUMN IF NOT EXISTS code text;
ALTER TABLE payment.institution_audit ADD COLUMN IF NOT EXISTS code text;
