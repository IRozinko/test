ALTER TABLE lending.loan
  ADD COLUMN first_disbursement_date date;

ALTER TABLE lending.loan_audit
  ADD COLUMN first_disbursement_date date;
