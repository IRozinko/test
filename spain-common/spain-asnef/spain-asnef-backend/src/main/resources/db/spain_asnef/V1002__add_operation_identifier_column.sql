alter table spain_asnef.log_row
  add column operation_identifier text;

update spain_asnef.log_row
set operation_identifier = loan_id;

alter table spain_asnef.log_row
  alter column operation_identifier set not null;
