SET search_path = alfa;

create table alfa.loan_rescheduling
(
    id                   bigint                      not null primary key,
    loan_id              bigint                      not null,
    status               text                        not null,
    number_of_payments   int                         not null,
    repayment_due_days   int                         not null,
    grace_period_days    int                         not null,
    installment_amount   numeric(19,4)               not null,
    reschedule_date      date                        not null,
    expire_date          date                        not null,
    entity_version       bigint                      not null,
    created_at           timestamp with time zone    not null,
    created_by           text,
    updated_at           timestamp with time zone    not null,
    updated_by           text
);

create table alfa.loan_rescheduling_audit
(
    id                   bigint,
    rev                  INTEGER,
    revtype              SMALLINT,

    status               text,
    loan_id              bigint,
    number_of_payments   int,
    repayment_due_days   int,
    grace_period_days    int,
    installment_amount   numeric(19,4),
    reschedule_date      date,
    expire_date          date,

    created_at           timestamp with time zone,
    created_by           text,
    updated_at           timestamp with time zone,
    updated_by           text
);

CREATE INDEX idx_loan_rescheduling_loan_id
  ON alfa.loan_rescheduling (loan_id);

