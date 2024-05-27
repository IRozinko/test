create table alfa_dpd_penalty_strategy
(
    id                      bigint                   not null primary key,
    created_at              timestamp with time zone not null,
    created_by              text,
    entity_version          bigint                   not null,
    updated_at              timestamp with time zone not null,
    updated_by              text,

    calculation_strategy_id bigint                   not null
);

create table alfa_dpd_penalty_strategy_penalty
(
    id                      bigint                   not null primary key,
    created_at              timestamp with time zone not null,
    created_by              text,
    entity_version          bigint                   not null,
    updated_at              timestamp with time zone not null,
    updated_by              text,

    dpd_penalty_strategy_id bigint                   not null references alfa_dpd_penalty_strategy (id),
    days_from               int,
    penalty_rate            numeric(9, 2)
);

create table alfa_dpd_penalty_strategy_audit
(
    id                      bigint,
    rev                     integer not null,
    revtype                 smallint,
    created_at              timestamp with time zone,
    created_by              text,
    entity_version          bigint,
    updated_at              timestamp with time zone,
    updated_by              text,

    calculation_strategy_id bigint
);

create table alfa_dpd_penalty_strategy_penalty_audit
(
    id                      bigint,
    rev                     integer not null,
    revtype                 smallint,
    created_at              timestamp with time zone,
    created_by              text,
    entity_version          bigint,
    updated_at              timestamp with time zone,
    updated_by              text,

    dpd_penalty_strategy_id bigint,
    days_from               int,
    penalty_rate            numeric(9, 2)
);
