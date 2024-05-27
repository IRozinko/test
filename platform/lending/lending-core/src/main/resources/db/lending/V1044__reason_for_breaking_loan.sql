ALTER TABLE lending.loan
  add column reason_for_break text;

ALTER TABLE lending.loan_audit
  add column reason_for_break text;
