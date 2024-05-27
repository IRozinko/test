create table terms
(
    id             bigint                   not null,
    created_at     timestamp with time zone not null,
    created_by     text,
    entity_version bigint                   not null,
    updated_at     timestamp with time zone not null,
    updated_by     text,

    name           text                     not null,
    text           text                     not null,
    version        text                     not null,
    changed_at     timestamp with time zone not null
);

create table terms_audit
(
    id             bigint,
    rev            integer not null,
    revtype        smallint,
    created_at     timestamp with time zone,
    created_by     text,
    entity_version bigint,
    updated_at     timestamp with time zone,
    updated_by     text,

    name           text,
    text           text,
    version        text,
    changed_at     timestamp with time zone
);


create table consent
(
    id             bigint                   not null,
    created_at     timestamp with time zone not null,
    created_by     text,
    entity_version bigint                   not null,
    updated_at     timestamp with time zone not null,
    updated_by     text,

    client_id      bigint                   not null,
    name           text                     not null,
    version        text                     not null,
    accepted       boolean                  not null default false,
    source         text                     not null,
    changed_at     timestamp with time zone not null
);

create index idx_consent_client_id on consent (client_id);

create table consent_audit
(
    id             bigint,
    rev            integer not null,
    revtype        smallint,
    created_at     timestamp with time zone,
    created_by     text,
    entity_version bigint,
    updated_at     timestamp with time zone,
    updated_by     text,

    client_id      bigint,
    name           text,
    version        text,
    accepted       boolean,
    source         text,
    changed_at     timestamp with time zone
);
