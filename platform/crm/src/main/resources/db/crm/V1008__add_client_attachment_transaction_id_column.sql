alter table crm.client_attachment
  add column transaction_id bigint;
alter table crm.client_attachment_audit
  add column transaction_id bigint;
