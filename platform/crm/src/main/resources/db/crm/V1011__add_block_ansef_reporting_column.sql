ALTER TABLE crm.client
  ADD COLUMN excluded_from_asnef BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE crm.client_audit
  ADD COLUMN excluded_from_asnef BOOLEAN;
