-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_client_account_number_trgm ON crm.client USING gin (account_number gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_client_number_trgm ON crm.client USING gin (client_number gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_created_at ON crm.client (created_at);
CREATE INDEX IF NOT EXISTS idx_client_document_number_trgm ON crm.client USING gin (document_number gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_first_name_trgm ON crm.client USING gin (first_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_last_name_trgm ON crm.client USING gin (last_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_phone_trgm ON crm.client USING gin (phone gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_second_last_name_trgm ON crm.client USING gin (second_last_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_segments_text_trgm ON crm.client USING gin (segments_text gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_client_attachment_transaction_id ON crm.client_attachment USING btree (transaction_id);

-- NEW INDEXES
DROP INDEX IF EXISTS crm.idx_email_contact_email;
CREATE INDEX IF NOT EXISTS idx_email_contact_email_trgm ON crm.email_contact USING gin (email gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_phone_contact_local_number_trgm ON crm.phone_contact USING gin (local_number gin_trgm_ops);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS crm.idx_client_attribute_client_id;
DROP INDEX IF EXISTS crm.idx_client_segment_client_id;
