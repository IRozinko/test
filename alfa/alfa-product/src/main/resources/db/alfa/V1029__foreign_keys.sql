ALTER TABLE instantor.response
  ADD CONSTRAINT fk_response_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE instantor.transaction
  ADD CONSTRAINT fk_transaction_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);


ALTER TABLE iovation.blackbox
  ADD CONSTRAINT fk_blackbox_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE iovation.transaction
  ADD CONSTRAINT fk_transaction_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id),
  ADD CONSTRAINT fk_transaction_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);

ALTER TABLE ONLY lending.loan_contract
  ADD CONSTRAINT loan_contract_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id),
  ADD CONSTRAINT loan_contract_source_transaction_id FOREIGN KEY (source_transaction_id) REFERENCES transaction.transaction (id);

ALTER TABLE nordigen.log
  ADD CONSTRAINT fk_log_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id),
  ADD CONSTRAINT fk_log_instantor_response_id FOREIGN KEY (instantor_response_id) REFERENCES instantor.response (id),
  ADD CONSTRAINT fk_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id),
  ADD CONSTRAINT fk_log_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);


ALTER TABLE spain_equifax.equifax
  ADD CONSTRAINT fk_equifax_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);
ALTER TABLE spain_equifax.equifax
  ADD CONSTRAINT fk_equifax_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);

ALTER TABLE spain_experian.cais_resumen
  ADD CONSTRAINT fk_cais_resumen_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE spain_experian.cais_resumen
  ADD CONSTRAINT fk_cais_resumen_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);
ALTER TABLE spain_experian.cais_operaciones
  ADD CONSTRAINT fk_cais_operaciones_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE spain_experian.cais_operaciones
  ADD CONSTRAINT fk_cais_operaciones_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);

ALTER TABLE spain_inglobaly.response
  ADD CONSTRAINT fk_response_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE spain_inglobaly.response
  ADD CONSTRAINT fk_response_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);

ALTER TABLE spain_scoring.log
  ADD CONSTRAINT fk_log_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id);
ALTER TABLE spain_scoring.log
  ADD CONSTRAINT fk_log_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE spain_scoring.log
  ADD CONSTRAINT fk_log_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application (id);
