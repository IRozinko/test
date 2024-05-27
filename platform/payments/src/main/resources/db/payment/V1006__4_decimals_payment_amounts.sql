ALTER TABLE payment.payment
  ALTER COLUMN amount TYPE numeric(19,4),
  ALTER COLUMN pending_amount TYPE numeric(19,4);

ALTER TABLE payment.payment_audit
  ALTER COLUMN amount TYPE numeric(19,4),
  ALTER COLUMN pending_amount TYPE numeric(19,4);
