ALTER TABLE ONLY lending.loan_application_attribute
  ADD CONSTRAINT loan_application_attribute_pkey PRIMARY KEY (loan_application_id, key),
  ADD CONSTRAINT fk_loan_application_attribute_client_id FOREIGN KEY (loan_application_id) REFERENCES lending.loan_application (id) ON DELETE CASCADE;

ALTER TABLE ONLY lending.loan_application_attribute_audit
  ADD CONSTRAINT fk_loan_application_attribute_audit_rev_id FOREIGN KEY (rev) REFERENCES common.revision (id);

CREATE INDEX IF NOT EXISTS idx_loan_application_attribute_client_id ON lending.loan_application_attribute USING btree (loan_application_id);
