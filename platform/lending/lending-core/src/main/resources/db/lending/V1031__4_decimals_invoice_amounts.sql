ALTER TABLE lending.invoice
  ALTER COLUMN total TYPE numeric(19,4),
  ALTER COLUMN total_paid TYPE numeric(19,4);

ALTER TABLE lending.invoice_audit
  ALTER COLUMN total TYPE numeric(19,4),
  ALTER COLUMN total_paid TYPE numeric(19,4);

ALTER TABLE lending.invoice_item
  ALTER COLUMN amount TYPE numeric(19,4),
  ALTER COLUMN amount_paid TYPE numeric(19,4);

ALTER TABLE lending.invoice_item_audit
  ALTER COLUMN amount TYPE numeric(19,4),
  ALTER COLUMN amount_paid TYPE numeric(19,4);
