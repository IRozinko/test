alter table email.log add column loan_id bigint;
alter table email.log add column loan_application_id bigint;
alter table email.log add column task_id bigint;
alter table email.log add column debt_id bigint;
alter table email.log add column send_from_name text not null default '';
