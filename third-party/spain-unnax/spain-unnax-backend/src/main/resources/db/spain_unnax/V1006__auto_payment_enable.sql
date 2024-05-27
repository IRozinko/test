ALTER TABLE spain_unnax.credit_card
    ADD COLUMN automatic_payment_enabled boolean DEFAULT FALSE;

ALTER TABLE spain_unnax.credit_card_audit
    ADD COLUMN automatic_payment_enabled boolean;
