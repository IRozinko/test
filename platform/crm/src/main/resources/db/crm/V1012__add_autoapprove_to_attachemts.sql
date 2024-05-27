ALTER TABLE crm.client_attachment
  ADD COLUMN auto_approve BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN auto_approve_term int;

ALTER TABLE crm.client_attachment_audit
  ADD COLUMN auto_approve BOOLEAN,
  ADD COLUMN auto_approve_term int;
