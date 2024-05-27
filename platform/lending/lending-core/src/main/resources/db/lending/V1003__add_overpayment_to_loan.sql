ALTER TABLE lending.loan
  ADD COLUMN overpayment_received NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_refunded NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_used NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_available NUMERIC(19, 4) NOT NULL DEFAULT 0;

ALTER TABLE lending.loan_audit
  ADD COLUMN overpayment_received NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_refunded NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_used NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN overpayment_available NUMERIC(19, 4) NOT NULL DEFAULT 0;
