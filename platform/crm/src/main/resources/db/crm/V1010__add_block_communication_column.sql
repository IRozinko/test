ALTER TABLE crm.client
  ADD COLUMN block_communication BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE crm.client_audit
  ADD COLUMN block_communication BOOLEAN;
