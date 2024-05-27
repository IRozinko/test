ALTER TABLE workflow.workflow ADD CONSTRAINT fk_workflow_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_workflow_loan_id ON workflow.workflow USING btree (loan_id);
ALTER TABLE workflow.workflow ADD CONSTRAINT fk_workflow_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_workflow_client_id ON workflow.workflow USING btree (client_id);
ALTER TABLE workflow.workflow ADD CONSTRAINT fk_workflow_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_workflow_application_id ON workflow.workflow USING btree (application_id);

ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id); CREATE INDEX IF NOT EXISTS idx_transaction_product_id ON transaction.transaction USING btree (product_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_payment_id FOREIGN KEY (payment_id) REFERENCES payment.payment (id); CREATE INDEX IF NOT EXISTS idx_transaction_payment_id ON transaction.transaction USING btree (payment_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_transaction_loan_id ON transaction.transaction USING btree (loan_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_invoice_id FOREIGN KEY (invoice_id) REFERENCES lending.invoice (id); CREATE INDEX IF NOT EXISTS idx_transaction_invoice_id ON transaction.transaction USING btree (invoice_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_institution_id FOREIGN KEY (institution_id) REFERENCES payment.institution (id); CREATE INDEX IF NOT EXISTS idx_transaction_institution_id ON transaction.transaction USING btree (institution_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_institution_account_id FOREIGN KEY (institution_account_id) REFERENCES payment.institution_account (id); CREATE INDEX IF NOT EXISTS idx_transaction_institution_account_id ON transaction.transaction USING btree (institution_account_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_disbursement_id FOREIGN KEY (disbursement_id) REFERENCES payment.disbursement (id); CREATE INDEX IF NOT EXISTS idx_transaction_disbursement_id ON transaction.transaction USING btree (disbursement_id);
ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_transaction_client_id ON transaction.transaction USING btree (client_id);

ALTER TABLE task.task ADD CONSTRAINT fk_task_workflow_id FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id); CREATE INDEX IF NOT EXISTS idx_task_workflow_id ON task.task USING btree (workflow_id);
ALTER TABLE task.task ADD CONSTRAINT fk_task_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_task_loan_id ON task.task USING btree (loan_id);
ALTER TABLE task.task ADD CONSTRAINT fk_task_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_task_client_id ON task.task USING btree (client_id);
ALTER TABLE task.task ADD CONSTRAINT fk_task_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_task_application_id ON task.task USING btree (application_id);
ALTER TABLE task.task ADD CONSTRAINT fk_task_activity_id FOREIGN KEY (activity_id) REFERENCES workflow.activity (id); CREATE INDEX IF NOT EXISTS idx_task_activity_id ON task.task USING btree (activity_id);

ALTER TABLE sms.log ADD CONSTRAINT fk_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_log_client_id ON sms.log USING btree (client_id);

ALTER TABLE rule.rule_set_log ADD CONSTRAINT fk_rule_set_log_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_rule_set_log_loan_id ON rule.rule_set_log USING btree (loan_id);
ALTER TABLE rule.rule_set_log ADD CONSTRAINT fk_rule_set_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_rule_set_log_client_id ON rule.rule_set_log USING btree (client_id);
ALTER TABLE rule.rule_set_log ADD CONSTRAINT fk_rule_set_log_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_rule_set_log_application_id ON rule.rule_set_log USING btree (application_id);
ALTER TABLE rule.rule_log ADD CONSTRAINT fk_rule_log_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_rule_log_loan_id ON rule.rule_log USING btree (loan_id);
ALTER TABLE rule.rule_log ADD CONSTRAINT fk_rule_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_rule_log_client_id ON rule.rule_log USING btree (client_id);
ALTER TABLE rule.rule_log ADD CONSTRAINT fk_rule_log_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_rule_log_application_id ON rule.rule_log USING btree (application_id);

ALTER TABLE payment.disbursement ADD CONSTRAINT fk_disbursement_institution_account_id FOREIGN KEY (institution_account_id) REFERENCES payment.institution_account (id); CREATE INDEX IF NOT EXISTS idx_disbursement_institution_account_id ON payment.disbursement USING btree (institution_account_id);
ALTER TABLE payment.disbursement ADD CONSTRAINT fk_disbursement_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_disbursement_loan_id ON payment.disbursement USING btree (loan_id);
ALTER TABLE payment.disbursement ADD CONSTRAINT fk_disbursement_exported_cloud_file_id FOREIGN KEY (exported_cloud_file_id) REFERENCES storage.cloud_file (id); CREATE INDEX IF NOT EXISTS idx_disbursement_exported_cloud_file_id ON payment.disbursement USING btree (exported_cloud_file_id);
ALTER TABLE payment.disbursement ADD CONSTRAINT fk_disbursement_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_disbursement_client_id ON payment.disbursement USING btree (client_id);
ALTER TABLE payment.statement ADD CONSTRAINT fk_statement_file_id FOREIGN KEY (file_id) REFERENCES storage.cloud_file (id); CREATE INDEX IF NOT EXISTS idx_statement_file_id ON payment.statement USING btree (file_id);

ALTER TABLE lending.loan_daily_snapshot ADD CONSTRAINT fk_loan_daily_snapshot_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id); CREATE INDEX IF NOT EXISTS idx_loan_daily_snapshot_product_id ON lending.loan_daily_snapshot USING btree (product_id);
ALTER TABLE lending.loan_daily_snapshot ADD CONSTRAINT fk_loan_daily_snapshot_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_loan_daily_snapshot_loan_id ON lending.loan_daily_snapshot USING btree (loan_id);
ALTER TABLE lending.loan_daily_snapshot ADD CONSTRAINT fk_loan_daily_snapshot_loan_application_id FOREIGN KEY (loan_application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_loan_daily_snapshot_loan_application_id ON lending.loan_daily_snapshot USING btree (loan_application_id);
ALTER TABLE lending.loan_daily_snapshot ADD CONSTRAINT fk_loan_daily_snapshot_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_loan_daily_snapshot_client_id ON lending.loan_daily_snapshot USING btree (client_id);
ALTER TABLE lending.loan_application ADD CONSTRAINT fk_loan_application_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id); CREATE INDEX IF NOT EXISTS idx_loan_application_product_id ON lending.loan_application USING btree (product_id);
ALTER TABLE lending.loan_application ADD CONSTRAINT fk_loan_application_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_loan_application_loan_id ON lending.loan_application USING btree (loan_id);
ALTER TABLE lending.loan_application ADD CONSTRAINT fk_loan_application_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_loan_application_client_id ON lending.loan_application USING btree (client_id);

ALTER TABLE lending.loan ADD CONSTRAINT fk_loan_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_loan_client_id ON lending.loan USING btree (client_id);
ALTER TABLE lending.invoice ADD CONSTRAINT fk_invoice_file_id FOREIGN KEY (file_id) REFERENCES storage.cloud_file (id); CREATE INDEX IF NOT EXISTS idx_invoice_file_id ON lending.invoice USING btree (file_id);
ALTER TABLE lending.invoice ADD CONSTRAINT fk_invoice_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_invoice_client_id ON lending.invoice USING btree (client_id);
ALTER TABLE lending.credit_limit ADD CONSTRAINT fk_credit_limit_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_credit_limit_client_id ON lending.credit_limit USING btree (client_id);

ALTER TABLE email.log ADD CONSTRAINT fk_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_log_client_id ON email.log USING btree (client_id);

ALTER TABLE crm.client_attachment ADD CONSTRAINT fk_client_attachment_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_client_attachment_loan_id ON crm.client_attachment USING btree (loan_id);
ALTER TABLE crm.client_attachment ADD CONSTRAINT fk_client_attachment_file_id FOREIGN KEY (file_id) REFERENCES storage.cloud_file (id); CREATE INDEX IF NOT EXISTS idx_client_attachment_file_id ON crm.client_attachment USING btree (file_id);
ALTER TABLE crm.client_attachment ADD CONSTRAINT fk_client_attachment_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_client_attachment_application_id ON crm.client_attachment USING btree (application_id);

ALTER TABLE affiliate.lead ADD CONSTRAINT fk_lead_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_lead_client_id ON affiliate.lead USING btree (client_id);
ALTER TABLE affiliate.lead ADD CONSTRAINT fk_lead_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_lead_application_id ON affiliate.lead USING btree (application_id);
ALTER TABLE affiliate.event ADD CONSTRAINT fk_event_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_event_loan_id ON affiliate.event USING btree (loan_id);
ALTER TABLE affiliate.event ADD CONSTRAINT fk_event_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_event_client_id ON affiliate.event USING btree (client_id);
ALTER TABLE affiliate.event ADD CONSTRAINT fk_event_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id); CREATE INDEX IF NOT EXISTS idx_event_application_id ON affiliate.event USING btree (application_id);

ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_transaction_id FOREIGN KEY (transaction_id) REFERENCES transaction.transaction (id); CREATE INDEX IF NOT EXISTS idx_entry_transaction_id ON accounting.entry USING btree (transaction_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id); CREATE INDEX IF NOT EXISTS idx_entry_product_id ON accounting.entry USING btree (product_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_payment_id FOREIGN KEY (payment_id) REFERENCES payment.payment (id); CREATE INDEX IF NOT EXISTS idx_entry_payment_id ON accounting.entry USING btree (payment_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_entry_loan_id ON accounting.entry USING btree (loan_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_invoice_id FOREIGN KEY (invoice_id) REFERENCES lending.invoice (id); CREATE INDEX IF NOT EXISTS idx_entry_invoice_id ON accounting.entry USING btree (invoice_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_institution_id FOREIGN KEY (institution_id) REFERENCES payment.institution (id); CREATE INDEX IF NOT EXISTS idx_entry_institution_id ON accounting.entry USING btree (institution_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_institution_account_id FOREIGN KEY (institution_account_id) REFERENCES payment.institution_account (id); CREATE INDEX IF NOT EXISTS idx_entry_institution_account_id ON accounting.entry USING btree (institution_account_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_disbursement_id FOREIGN KEY (disbursement_id) REFERENCES payment.disbursement (id); CREATE INDEX IF NOT EXISTS idx_entry_disbursement_id ON accounting.entry USING btree (disbursement_id);
ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_entry_client_id ON accounting.entry USING btree (client_id);

ALTER TABLE dc.debt ADD CONSTRAINT fk_debt_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id); CREATE INDEX IF NOT EXISTS idx_debt_loan_id ON dc.debt USING btree (loan_id);
ALTER TABLE dc.debt ADD CONSTRAINT fk_debt_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_debt_client_id ON dc.debt USING btree (client_id);

ALTER TABLE activity.activity ADD CONSTRAINT fk_activity_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id); CREATE INDEX IF NOT EXISTS idx_activity_client_id ON activity.activity USING btree (client_id);
