ALTER TABLE crm.client_attachment
  ADD COLUMN attachment_sub_type TEXT NULL;
ALTER TABLE crm.client_attachment_audit
  ADD COLUMN attachment_sub_type TEXT NULL;
