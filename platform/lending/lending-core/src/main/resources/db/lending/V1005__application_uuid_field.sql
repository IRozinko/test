ALTER TABLE lending.loan_application
  ADD COLUMN uuid TEXT NOT NULL DEFAULT '';
UPDATE lending.loan_application SET uuid = md5(random()::text || clock_timestamp()::text)::text;

ALTER TABLE lending.loan_application_audit
  ADD COLUMN uuid TEXT;

