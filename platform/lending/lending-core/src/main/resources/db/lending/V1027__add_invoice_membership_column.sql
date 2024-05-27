ALTER TABLE lending.invoice
  ADD COLUMN membership_level_changed BOOLEAN;

ALTER TABLE lending.invoice_audit
  ADD COLUMN membership_level_changed BOOLEAN;
