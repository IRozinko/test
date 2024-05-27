set search_path = alfa;

alter table alfa.viventor_extension_log
    add column principal NUMERIC(19, 2);
alter table alfa.viventor_extension_log
    add column interest_rate NUMERIC(5, 2);
alter table alfa.viventor_extension_log
    add column start_date date;
