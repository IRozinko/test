alter table crm.email_contact
    add column verified boolean not null default false,
    add column verified_at timestamp with time zone;

alter table crm.email_contact_audit
    add column verified boolean,
    add column verified_at timestamp with time zone;
