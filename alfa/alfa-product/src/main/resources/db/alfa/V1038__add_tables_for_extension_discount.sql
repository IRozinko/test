SET search_path = alfa;

create table alfa.extension_discount
(
    id                 bigint                      not null primary key,
    effective_from     date                        not null,
    effective_to       date                        not null,
    rate_in_percent    NUMERIC(19, 2)              not null,
    loan_id            bigint                      not null,
    active             boolean                     not null,

    entity_version     bigint                      not null,
    created_at         timestamp with time zone    not null,
    created_by         text,
    updated_at         timestamp with time zone    not null,
    updated_by         text
);

create table alfa.extension_discount_audit
(
    id                 bigint,
    rev                INTEGER,
    revtype            SMALLINT,

    effective_from     date,
    effective_to       date,
    rate_in_percent    NUMERIC(19, 2),
    loan_id            bigint,
    active             boolean,
    created_at         timestamp with time zone,
    created_by         text,
    updated_at         timestamp with time zone,
    updated_by         text
);

CREATE INDEX idx_extesion_discount_loan_id
  ON alfa.extension_discount (loan_id);

