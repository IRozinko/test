create table cms.locale
(
    id             bigint                   not null,
    created_at     timestamp with time zone not null,
    created_by     text,
    entity_version bigint                   not null,
    updated_at     timestamp with time zone not null,
    updated_by     text,

    locale         text unique              not null,
    is_default     boolean                  not null default false
);

create unique index idx_unique_default_locale on cms.locale (is_default) where is_default;

create table cms.locale_audit
(
    id         bigint,
    rev        integer,
    revtype    smallint,
    created_at timestamp with time zone,
    created_by text,
    updated_at timestamp with time zone,
    updated_by text,

    locale     text,
    is_default boolean
);

insert into cms.locale(id, created_at, created_by, entity_version, updated_at, updated_by, locale, is_default)
values (nextval('common.id_seq'), now(), 'migration', 0, now(), 'migration', 'es', true);

alter table cms.item
    add column locale text not null default 'es';
alter table cms.item_audit
    add column locale text;


-- find name for unique constraint and drop it
do
$$
    declare
        r record;
    begin
        for r in SELECT conname
                 FROM pg_constraint
                 WHERE conrelid = 'cms.item'::regclass
                   AND contype = 'u'
            loop
                execute 'alter table cms.item drop constraint ' || r.conname || ';';
            end loop;
    end
$$;

alter table cms.item
    add constraint u_item_key_locale unique (item_key, locale);
