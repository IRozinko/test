ALTER TABLE payment.institution ADD COLUMN disabled boolean NOT NULL DEFAULT FALSE;
ALTER TABLE payment.institution_audit ADD COLUMN disabled boolean NOT NULL DEFAULT FALSE;
