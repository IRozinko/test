ALTER TABLE lending.loan
  ADD COLUMN rescheduled_date DATE;

ALTER TABLE lending.loan
  ADD COLUMN reschedule_broken_date DATE;

ALTER TABLE lending.loan
  ADD COLUMN moved_to_legal_date DATE;

ALTER TABLE lending.loan_audit
  ADD COLUMN rescheduled_date DATE;

ALTER TABLE lending.loan_audit
  ADD COLUMN reschedule_broken_date DATE;

ALTER TABLE lending.loan_audit
  ADD COLUMN moved_to_legal_date DATE;
