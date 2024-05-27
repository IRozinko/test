ALTER TABLE crm.client_address
  ADD COLUMN is_primary BOOLEAN NULL DEFAULT true;
ALTER TABLE crm.client_address_audit
  ADD COLUMN is_primary BOOLEAN NULL DEFAULT true;
