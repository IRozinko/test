SET search_path = web;

DROP INDEX IF EXISTS idx_special_link_client_id_special_link_type;

CREATE INDEX IF NOT EXISTS idx_special_link_client_id_special_link_type ON special_link USING btree (client_id, special_link_type);
