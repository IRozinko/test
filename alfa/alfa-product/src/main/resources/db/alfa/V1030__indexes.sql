-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_wealthiness_client_id ON alfa.wealthiness USING btree (client_id);
CREATE INDEX IF NOT EXISTS idx_wealthiness_instantor_response_id ON alfa.wealthiness USING btree (instantor_response_id);
CREATE INDEX IF NOT EXISTS idx_wealthiness_nordigen_log_id ON alfa.wealthiness USING btree (nordigen_log_id);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS alfa.idx_spain_address_city;
DROP INDEX IF EXISTS alfa.idx_spain_address_postal_code;
DROP INDEX IF EXISTS alfa.idx_spain_address_province;
DROP INDEX IF EXISTS alfa.idx_popup_attribute_client_id;
DROP INDEX IF EXISTS alfa.idx_popup_client_id;
DROP INDEX IF EXISTS alfa.idx_popup_client_id_popup_type;
