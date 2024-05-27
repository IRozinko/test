ALTER TABLE ONLY notification.notification
  ADD CONSTRAINT fk_notification_client_id FOREIGN KEY (client_id) REFERENCES crm.client(id),
  ADD CONSTRAINT fk_notification_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan(id),
  ADD CONSTRAINT fk_notification_loan_application_id FOREIGN KEY (loan_application_id) REFERENCES lending.loan_application(id),
  ADD CONSTRAINT fk_notification_task_id FOREIGN KEY (task_id) REFERENCES task.task(id),
  ADD CONSTRAINT fk_notification_debt_id FOREIGN KEY (debt_id) REFERENCES dc.debt(id);

ALTER TABLE ONLY email.log
  DROP COLUMN cms_key,
  DROP COLUMN loan_id,
  DROP COLUMN loan_application_id,
  DROP COLUMN task_id,
  DROP COLUMN debt_id,
  DROP COLUMN client_id;

ALTER TABLE ONLY sms.log
  DROP COLUMN cms_key,
  DROP COLUMN loan_id,
  DROP COLUMN loan_application_id,
  DROP COLUMN task_id,
  DROP COLUMN debt_id,
  DROP COLUMN client_id;
