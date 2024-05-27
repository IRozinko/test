alter table spain_unnax.credit_card
    drop column client_id;

alter table spain_unnax.credit_card_audit
    drop column client_id;

ALTER TABLE spain_unnax.credit_card
    ADD COLUMN client_number TEXT;

ALTER TABLE spain_unnax.credit_card_audit
  ADD COLUMN client_number TEXT;
