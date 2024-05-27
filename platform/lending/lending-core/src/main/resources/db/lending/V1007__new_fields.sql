ALTER TABLE lending.loan
  ADD COLUMN broken_date DATE;

ALTER TABLE lending.loan_audit
  ADD COLUMN broken_date DATE;


ALTER TABLE lending.fee
  ADD COLUMN extension_period_unit text not null;
ALTER TABLE lending.fee
  ADD COLUMN extension_period_count int8 not null;

ALTER TABLE lending.fee_audit
  ADD COLUMN extension_period_unit text not null;
ALTER TABLE lending.fee_audit
  ADD COLUMN extension_period_count int8 not null;
