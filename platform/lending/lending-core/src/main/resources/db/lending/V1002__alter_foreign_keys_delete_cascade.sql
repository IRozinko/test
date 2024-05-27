
ALTER TABLE lending.invoice_item
  DROP CONSTRAINT fkbu6tmpd0mtgu9wrw5bj5uv09v, -- duplicate fk
  DROP CONSTRAINT fk_invoice_item_invoice_id,
  ADD CONSTRAINT fk_invoice_item_invoice_id
    FOREIGN KEY (invoice_id) REFERENCES lending.invoice (id) ON DELETE CASCADE;
