ALTER TABLE crm.client
  ADD COLUMN transferred_to_loc BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE crm.client_audit
  ADD COLUMN transferred_to_loc BOOLEAN;
