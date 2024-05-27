alter table lending.loan
    add column compliant_with_aemip boolean default false not null;

alter table lending.loan_audit
    add column compliant_with_aemip boolean default false;
