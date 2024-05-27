create table lending.promo_code
(
    id                 bigint                      not null primary key,
    code               text                        not null,
    description        text,
    effective_from     date                        not null,
    effective_to       date                        not null,
    rate_in_percent    NUMERIC(19, 2)              not null,
    max_times_to_apply bigint                      not null,
    new_clients_only   boolean,

    entity_version     bigint                      not null,
    created_at         timestamp without time zone not null,
    created_by         text,
    updated_at         timestamp without time zone not null,
    updated_by         text
);

create table lending.promo_code_audit
(
    id                 bigint,
    rev                INTEGER,
    revtype            SMALLINT,

    code               text,
    description        text,
    effective_from     date,
    effective_to       date,
    rate_in_percent    NUMERIC(19, 2),
    max_times_to_apply bigint,
    new_clients_only   boolean,

    created_at         timestamp without time zone,
    created_by         text,
    updated_at         timestamp without time zone,
    updated_by         text
);

create table lending.promo_code_client
(
    id             bigint not null primary key,

    promo_code_id  bigint not null,
    client_number  text   not null,

    entity_version bigint not null,
    created_at     timestamp without time zone,
    created_by     text,
    updated_at     timestamp without time zone,
    updated_by     text
);

create table lending.promo_code_client_audit
(
    id            bigint,
    rev           INTEGER,
    revtype       SMALLINT,

    promo_code_id bigint,
    client_number  text,

    created_at    timestamp without time zone,
    created_by    text,
    updated_at    timestamp without time zone,
    updated_by    text
);

alter table lending.loan_application
    add column promo_code_id bigint references lending.promo_code (id);

alter table lending.loan_application_audit
    add column promo_code_id bigint references lending.promo_code (id);

alter table lending.loan
    add column promo_code_id bigint references lending.promo_code (id);

alter table lending.loan_audit
    add column promo_code_id bigint references lending.promo_code (id);
