create table spain_unnax.bank_statements_request
(
    id             bigint                   NOT NULL PRIMARY KEY,
    created_at     timestamp with time zone NOT NULL,
    created_by     text,
    entity_version bigint                   NOT NULL,
    updated_at     timestamp with time zone NOT NULL,
    updated_by     text,

    from_date      date                     NOT NULL,
    to_date        date                     NOT NULL,
    iban           text                     NOT NULL,
    request_code   text                     NOT NULL,
    status         text                     NOT NULL,
    error          text,
    processed_at   timestamp with time zone
);

create table spain_unnax.bank_statements_request_audit
(
    id             bigint                   NOT NULL PRIMARY KEY,
    rev            INT4                     NOT NULL,
    revtype        INT2,
    created_at     timestamp with time zone NOT NULL,
    created_by     text,
    entity_version bigint                   NOT NULL,
    updated_at     timestamp with time zone NOT NULL,
    updated_by     text,

    from_date      date,
    to_date        date,
    iban           text,
    request_code   text,
    status         text,
    error          text,
    processed_at   timestamp with time zone
)
