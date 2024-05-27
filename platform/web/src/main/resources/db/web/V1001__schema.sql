CREATE TABLE web.special_link (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  client_id                     INT8           NOT NULL,
  token                         TEXT           NOT NULL,
  special_link_type             TEXT           NOT NULL,
  reusable                      BOOLEAN        NOT NULL,
  auto_login_required           BOOLEAN        NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_special_link_token ON web.special_link USING btree (token);
CREATE UNIQUE INDEX IF NOT EXISTS idx_special_link_client_id_special_link_type ON web.special_link USING btree (client_id, special_link_type);

CREATE TABLE web.special_link_audit (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  client_id                     INT8           NOT NULL,
  token                         TEXT           NOT NULL,
  special_link_type             TEXT           NOT NULL,
  reusable                      BOOLEAN        NOT NULL,
  auto_login_required           BOOLEAN        NOT NULL,
  PRIMARY KEY (id)
);
