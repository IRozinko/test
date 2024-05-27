ALTER TABLE crm.verify_email_token
  DROP CONSTRAINT fk_verify_email_token_client_id,
  ADD CONSTRAINT fk_verify_email_token_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.reset_password_token
  DROP CONSTRAINT fk_reset_password_token_client_id,
  ADD CONSTRAINT fk_reset_password_token_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.phone_verification
  DROP CONSTRAINT fk_phone_verification_phone_contact_id,
  ADD CONSTRAINT fk_phone_verification_phone_contact_id FOREIGN KEY (phone_contact_id) REFERENCES crm.phone_contact (id) ON DELETE CASCADE;

ALTER TABLE crm.phone_verification
  DROP CONSTRAINT fk_phone_verification_client_id,
  ADD CONSTRAINT fk_phone_verification_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.phone_contact
  DROP CONSTRAINT fk_phone_contact_client_id,
  ADD CONSTRAINT fk_phone_contact_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.identity_document
  DROP CONSTRAINT fk_identity_document_client_id,
  ADD CONSTRAINT fk_identity_document_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.email_login
  DROP CONSTRAINT fk_email_login_client_id,
  ADD CONSTRAINT fk_email_login_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.email_contact
  DROP CONSTRAINT fk_email_contact_client_id,
  ADD CONSTRAINT fk_email_contact_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.client_segment
  DROP CONSTRAINT fk_client_segment_client_id,
  ADD CONSTRAINT fk_client_segment_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.client_bank_account
  DROP CONSTRAINT fk_client_bank_account_client_id,
  ADD CONSTRAINT fk_client_bank_account_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.client_attribute
  DROP CONSTRAINT fk_client_attribute_client_id,
  ADD CONSTRAINT fk_client_attribute_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.client_attachment
  DROP CONSTRAINT fk_client_attachment_client_id,
  ADD CONSTRAINT fk_client_attachment_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;

ALTER TABLE crm.client_address
  DROP CONSTRAINT fk_client_address_client_id,
  ADD CONSTRAINT fk_client_address_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id) ON DELETE CASCADE;
