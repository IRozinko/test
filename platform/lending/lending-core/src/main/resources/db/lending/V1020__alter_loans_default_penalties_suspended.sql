
ALTER TABLE ONLY lending.loan ALTER COLUMN penalty_suspended SET DEFAULT FALSE;
ALTER TABLE ONLY lending.loan_audit ALTER COLUMN penalty_suspended SET DEFAULT FALSE;
