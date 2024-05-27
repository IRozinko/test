create table spain_crosscheck.log (
    id int8 not null,
    created_at TIMESTAMP WITH TIME ZONE not null,
    created_by text,
    entity_version int8 not null,
    updated_at TIMESTAMP WITH TIME ZONE not null,
    updated_by text,
    application_id int8,
    blacklisted boolean not null,
    client_id int8,
    dni text,
    error text,
    loan_id int8,
    max_dpd int8 not null,
    open_loans int8 not null,
    repeated_client boolean not null,
    response_body text,
    response_status_code int4 not null,
    status text not null,
    primary key (id)
);

create index idx_log_client_id on spain_crosscheck.log (client_id);
create index idx_log_application_id on spain_crosscheck.log (application_id);
