alter table crm.client
    add column locale text not null default 'es';

alter table crm.client_audit
    add column locale text default 'es';
