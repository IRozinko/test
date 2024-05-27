set search_path = alfa;

alter table alfa.viventor_loan_data
    add column in_test_group boolean default false;

alter table alfa.viventor_loan_data_audit
    add column in_test_group boolean default false;

create table alfa.viventor_export_settings
(
    version       bigint not null,
    p_value        numeric(9,2) not null
);
