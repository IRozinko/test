ALTER TABLE payment.payment
    ADD COLUMN IF NOT EXISTS bank_order_code text;
ALTER TABLE payment.payment_audit
    ADD COLUMN IF NOT EXISTS bank_order_code text;

CREATE INDEX idx_payment_bank_order_code ON payment.payment (bank_order_code);
