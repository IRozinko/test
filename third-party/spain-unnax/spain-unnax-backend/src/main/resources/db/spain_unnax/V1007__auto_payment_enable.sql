ALTER TABLE spain_unnax.credit_card
    ADD COLUMN bin INT8;
ALTER TABLE spain_unnax.credit_card
    ADD COLUMN pan TEXT;

ALTER TABLE spain_unnax.credit_card_audit
  ADD COLUMN bin INT8;
ALTER TABLE spain_unnax.credit_card_audit
  ADD COLUMN pan TEXT;
