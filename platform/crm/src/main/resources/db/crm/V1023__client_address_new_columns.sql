alter table crm.client_address
    add column house_floor text,
    add column house_letter text;

alter table crm.client_address_audit
    add column house_floor text,
    add column house_letter text;
