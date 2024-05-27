ALTER TABLE payment.disbursement ADD COLUMN auto_export boolean NOT NULL DEFAULT false ;
ALTER TABLE payment.disbursement_audit ADD COLUMN auto_export boolean;

ALTER TABLE payment.disbursement ADD CONSTRAINT payment_disbursement_reference_unique UNIQUE (reference);

ALTER TABLE payment.disbursement ALTER COLUMN institution_id DROP NOT NULL;
