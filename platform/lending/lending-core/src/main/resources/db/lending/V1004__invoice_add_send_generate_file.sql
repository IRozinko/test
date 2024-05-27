ALTER TABLE lending.invoice
  ADD COLUMN sent_at timestamp without time zone,
  ADD COLUMN generate_file boolean NOT NULL DEFAULT false,
  ADD COLUMN send_file boolean NOT NULL DEFAULT false
;

ALTER TABLE lending.invoice_audit
  ADD COLUMN sent_at timestamp without time zone,
  ADD COLUMN generate_file boolean NOT NULL DEFAULT false,
  ADD COLUMN send_file boolean NOT NULL DEFAULT false
;
