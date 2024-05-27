ALTER TABLE crm.email_login
  ADD COLUMN temporary_password BOOLEAN NULL DEFAULT false;
ALTER TABLE crm.email_login_audit
  ADD COLUMN temporary_password BOOLEAN;

update crm.email_login set temporary_password = false;
