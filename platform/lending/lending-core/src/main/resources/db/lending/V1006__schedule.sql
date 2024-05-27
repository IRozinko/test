   create table lending.schedule (
        id int8 not null,
        created_at TIMESTAMP WITH TIME ZONE not null,
        created_by text,
        entity_version int8 not null,
        updated_at TIMESTAMP WITH TIME ZONE not null,
        updated_by text,
        client_id int8 not null,
        close_loan_on_paid boolean not null,
        extension_period_count int8 not null,
        extension_period_unit text,
        grace_period_in_days int8 not null,
        installments int8 not null,
        invoice_applied_fees boolean not null,
        invoice_applied_interest boolean not null,
        invoice_applied_penalty boolean not null,
        latest boolean not null,
        maturity_date date not null,
        period_count int8 not null,
        period_unit text not null,
        previous_payment_schedule_id int8,
        base_overdue_days int8 not null,
        source_transaction_id int8 not null,
        source_transaction_type text not null,
        start_date date not null,
        loan_id int8 not null,
        primary key (id)
    );

    create table lending.schedule_audit (
        id int8 not null,
        rev int4 not null,
        revtype int2,
        created_at TIMESTAMP WITH TIME ZONE,
        created_by text,
        updated_at TIMESTAMP WITH TIME ZONE,
        updated_by text,
        client_id int8,
        close_loan_on_paid boolean,
        extension_period_count int8,
        extension_period_unit text,
        grace_period_in_days int8,
        installments int8,
        invoice_applied_fees boolean,
        invoice_applied_interest boolean,
        invoice_applied_penalty boolean,
        latest boolean,
        maturity_date date,
        period_count int8,
        period_unit text,
        previous_payment_schedule_id int8,
        source_transaction_id int8,
        base_overdue_days int8,
        source_transaction_type text,
        start_date date,
        loan_id int8,
        primary key (id, rev)
    );

create index idx_payment_schedule_client_id on lending.schedule (client_id);
create index idx_payment_schedule_loan_id on lending.schedule (loan_id);

    alter table lending.schedule
        add constraint FK5rb7tygstxplvw0a6veeupfqq
        foreign key (loan_id)
        references lending.loan;

    alter table lending.schedule_audit
        add constraint FKt6i7gbd159np4x2oxe9xnb4xc
        foreign key (rev)
        references common.revision;
