create table lending.promo_code_source
(
    id             bigint not null primary key,
    entity_version bigint not null,
    created_at     timestamp without time zone,
    created_by     text,
    updated_at     timestamp without time zone,
    updated_by     text,

    promo_code_id  bigint not null references lending.promo_code (id),
    source         text   not null
);

create table lending.promo_code_source_audit
(
    id             bigint,
    entity_version bigint,
    revtype        smallint,
    rev            smallint,
    created_at     timestamp without time zone,
    created_by     text,
    updated_at     timestamp without time zone,
    updated_by     text,

    promo_code_id  bigint,
    source         text
);

insert into lending.promo_code_source (promo_code_id, source, entity_version, created_at, created_by)
    (select id, source, 1, now(), 'migration' from lending.promo_code where source is not null);

alter table lending.promo_code
    drop column source;

alter table lending.promo_code_audit
    drop column source;
