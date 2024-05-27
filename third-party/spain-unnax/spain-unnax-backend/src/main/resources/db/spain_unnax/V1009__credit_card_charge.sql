create table spain_unnax.card_charge_request
(
    id                  bigint                   NOT NULL PRIMARY KEY,
    created_at          timestamp with time zone NOT NULL,
    created_by          text,
    entity_version      bigint                   NOT NULL,
    updated_at          timestamp with time zone NOT NULL,
    updated_by          text,

    client_id           bigint                   not null,
    order_code          text                     not null,
    amount              integer                  not null,
    concept             text                     not null,
    card_hash           text                     not null,
    card_hash_reference text                     not null,
    status              text                     not null,
    error               text
);

create table spain_unnax.card_charge_request_audit
(
    id                  bigint                   NOT NULL PRIMARY KEY,
    rev                 INT4                     NOT NULL,
    revtype             INT2,
    created_at          timestamp with time zone NOT NULL,
    created_by          text,
    entity_version      bigint                   NOT NULL,
    updated_at          timestamp with time zone NOT NULL,
    updated_by          text,

    client_id           bigint,
    order_code          text,
    amount              integer,
    concept             text,
    card_hash           text,
    card_hash_reference text,
    status              text,
    error               text
)
