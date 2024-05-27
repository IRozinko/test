ALTER TABLE lending.loan
  ADD COLUMN extensions INTEGER not null DEFAULT 0,
  ADD COLUMN extended_by_days INTEGER not null DEFAULT 0
;

ALTER TABLE lending.loan_audit
  ADD COLUMN extensions INTEGER not null DEFAULT 0,
  ADD COLUMN extended_by_days INTEGER not null DEFAULT 0
;
