ALTER TABLE lending.loan_application
  ADD COLUMN offer_approved_at TIMESTAMP WITH TIME ZONE,
  ADD COLUMN offer_approved_by TEXT,
  ADD COLUMN offer_approved_from_ip_address TEXT;

ALTER TABLE lending.loan_application_audit
  ADD COLUMN offer_approved_at TIMESTAMP WITH TIME ZONE,
  ADD COLUMN offer_approved_by TEXT,
  ADD COLUMN offer_approved_from_ip_address TEXT;
