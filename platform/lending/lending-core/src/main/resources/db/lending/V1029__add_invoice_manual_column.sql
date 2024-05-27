ALTER TABLE lending.invoice
  ADD COLUMN manual bool NOT NULL DEFAULT FALSE;

ALTER TABLE lending.invoice_audit
  ADD COLUMN manual bool;
