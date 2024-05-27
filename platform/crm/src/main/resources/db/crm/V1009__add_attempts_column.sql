alter table crm.phone_verification
  add column attempts integer NOT NULL DEFAULT 0;
alter table crm.phone_verification_audit
  add column attempts integer NOT NULL DEFAULT 0;
