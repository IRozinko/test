ALTER TABLE payment.institution ADD COLUMN IF NOT EXISTS statement_auto_exporter text;
ALTER TABLE payment.institution_audit ADD COLUMN IF NOT EXISTS statement_auto_exporter text;
