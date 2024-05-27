SET search_path = alfa;

alter table alfa.alfa_monthly_interest_strategy
    add column using_decision_engine boolean not null default false;
alter table alfa.alfa_monthly_interest_strategy
    add column scenario text;

alter table alfa.alfa_monthly_interest_strategy_audit
    add column using_decision_engine boolean default false;
alter table alfa.alfa_monthly_interest_strategy_audit
    add column scenario text;
