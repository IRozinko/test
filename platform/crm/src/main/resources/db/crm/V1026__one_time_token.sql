set search_path to crm;

create table one_time_token
(
    id             bigint                   not null,
    created_at     timestamp with time zone not null,
    created_by     text,
    entity_version bigint                   not null,
    updated_at     timestamp with time zone not null,
    updated_by     text,
    client_id      bigint                   not null,
    token          text                     not null,
    token_type     text                     not null,
    expires_at     timestamp with time zone null,
    used_at        timestamp with time zone null

);

create table one_time_token_audit
(
    id         bigint,
    rev        integer,
    revtype    smallint,
    created_at timestamp with time zone,
    created_by text,
    updated_at timestamp with time zone,
    updated_by text,
    client_id  bigint,
    token      text,
    token_type text,
    expires_at timestamp with time zone,
    used_at    timestamp with time zone
);
