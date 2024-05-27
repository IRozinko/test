ALTER TABLE lending.loan
  ADD COLUMN interest_discount_percent NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN interest_discount_amount NUMERIC(19, 4) NOT NULL DEFAULT 0;


ALTER TABLE lending.loan_audit
  ADD COLUMN interest_discount_percent NUMERIC(19, 4),
  ADD COLUMN interest_discount_amount NUMERIC(19, 4);
