ALTER TABLE transaction.transaction
 drop column early_repayment_received;

ALTER TABLE transaction.transaction_audit
  drop column early_repayment_received;
