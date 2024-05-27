ALTER TABLE transaction.transaction add column principal_scheduled numeric(19,4) NOT NULL DEFAULT 0;
ALTER TABLE transaction.transaction add column interest_scheduled numeric(19,4) NOT NULL DEFAULT 0;
ALTER TABLE transaction.transaction add column penalty_scheduled numeric(19,4) NOT NULL DEFAULT 0;
ALTER TABLE transaction.transaction add column fee_scheduled numeric(19,4) NOT NULL DEFAULT 0;
ALTER TABLE transaction.transaction add column schedule_id bigint;
ALTER TABLE transaction.transaction add column installment_id bigint;

ALTER TABLE transaction.transaction_audit add column principal_scheduled numeric(19,4);
ALTER TABLE transaction.transaction_audit add column interest_scheduled numeric(19,4);
ALTER TABLE transaction.transaction_audit add column penalty_scheduled numeric(19,4);
ALTER TABLE transaction.transaction_audit add column fee_scheduled numeric(19,4);
ALTER TABLE transaction.transaction_audit add column schedule_id bigint;
ALTER TABLE transaction.transaction_audit add column installment_id bigint;


ALTER TABLE transaction.transaction_entry add column amount_scheduled numeric(19,4) NOT NULL DEFAULT 0;
ALTER TABLE transaction.transaction_entry_audit add column amount_scheduled numeric(19,4);
