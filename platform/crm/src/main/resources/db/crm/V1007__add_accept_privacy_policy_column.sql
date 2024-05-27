alter table crm.client
  add column accept_privacy_policy boolean not null default false;

alter table crm.client_audit
  add column accept_privacy_policy boolean;
