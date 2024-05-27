ALTER TABLE payment.institution
    RENAME COLUMN statement_auto_exporter TO statement_api_exporter;

ALTER TABLE payment.institution_audit
    RENAME COLUMN statement_auto_exporter TO statement_api_exporter;


ALTER TABLE payment.disbursement
    RENAME COLUMN auto_export TO api_export;

ALTER TABLE payment.disbursement_audit
    RENAME COLUMN auto_export TO api_export;
