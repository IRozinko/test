ALTER TABLE lending.loan
  drop column early_repayment_received;

ALTER TABLE lending.loan_audit
  drop column early_repayment_received;
