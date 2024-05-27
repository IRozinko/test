ALTER TABLE lending.loan
  ADD COLUMN penalty_suspended BOOLEAN;

ALTER TABLE lending.loan_audit
  ADD COLUMN penalty_suspended BOOLEAN;
