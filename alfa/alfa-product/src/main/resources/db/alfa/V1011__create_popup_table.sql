CREATE TABLE alfa.popup (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  client_id                     INT8           NOT NULL,
  popup_type                    TEXT           NOT NULL,
  resolution                    TEXT           NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE alfa.popup_audit (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  client_id                     INT8           NOT NULL,
  popup_type                    TEXT           NOT NULL,
  resolution                    TEXT           NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_popup_client_id ON alfa.popup USING btree (client_id);
CREATE INDEX IF NOT EXISTS idx_popup_client_id_resolution ON alfa.popup USING btree (client_id, resolution);
CREATE INDEX IF NOT EXISTS idx_popup_client_id_popup_type ON alfa.popup USING btree (client_id, popup_type);
CREATE INDEX IF NOT EXISTS idx_popup_client_id_popup_type_resolution ON alfa.popup USING btree (client_id, popup_type, resolution);
