create table viventor_extension_log
(
    id                     bigint                   not null primary key,
    created_at             timestamp with time zone not null,
    created_by             text,
    entity_version         bigint                   not null,
    updated_at             timestamp with time zone not null,
    updated_by             text,

    loan_id                bigint                   not null,
    viventor_loan_id       text                     not null,
    local_maturity_date    date                     not null,
    viventor_maturity_date date                     not null,
    extension_term_days    bigint                   not null default 0
);
