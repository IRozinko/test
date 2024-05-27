ALTER TABLE lending.loan_application
  add column referer text;

ALTER TABLE lending.loan_application_audit
    add column referer text;
