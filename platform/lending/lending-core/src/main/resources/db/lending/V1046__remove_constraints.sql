alter table lending.loan
    alter column loan_application_id drop not null;
alter table lending.loan_contract
    alter column application_id drop not null;

alter table lending.loan_contract_audit
    alter column application_id drop not null;
