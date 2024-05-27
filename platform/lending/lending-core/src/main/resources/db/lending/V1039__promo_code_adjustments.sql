alter table lending.promo_code
    add column active boolean not null default false,
    add column source text;

alter table lending.promo_code_audit
    add column active boolean,
    add column source text;

update lending.promo_code set active = true;
