ALTER TABLE lending.loan_application
  ADD COLUMN requested_interest_discount_percent NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN offered_interest_discount_percent NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN offered_interest_discount_amount NUMERIC(19, 4) NOT NULL DEFAULT 0;


ALTER TABLE lending.loan_application_audit
  ADD COLUMN requested_interest_discount_percent NUMERIC(19, 4),
  ADD COLUMN offered_interest_discount_percent NUMERIC(19, 4),
  ADD COLUMN offered_interest_discount_amount NUMERIC(19, 4);
