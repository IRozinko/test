CREATE TABLE alfa.address (
  id          BIGSERIAL NOT NULL,
  city        TEXT      NOT NULL DEFAULT '',
  postal_code TEXT      NOT NULL DEFAULT '',
  province    TEXT      NOT NULL DEFAULT '',
  state       TEXT      NOT NULL DEFAULT '',
  PRIMARY KEY (id)
);

CREATE INDEX idx_alfa_address_postal_code
  ON alfa.address (postal_code);
CREATE INDEX idx_alfa_address_province
  ON alfa.address (province);
CREATE INDEX idx_alfa_address_city
  ON alfa.address (city);


CREATE TABLE alfa.wealthiness (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  account_number                TEXT           NOT NULL,
  client_id                     INT8           NOT NULL,
  instantor_response_id         INT8           NOT NULL,
  manual_weighted_wealthiness   NUMERIC(19, 2) NOT NULL,
  months                        INT4           NOT NULL,
  nordigen_log_id               INT8           NOT NULL,
  nordigen_weighted_wealthiness NUMERIC(19, 2) NOT NULL,
  period_from                   DATE           NOT NULL,
  period_to                     DATE           NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE alfa.wealthiness_audit (
  id                            INT8 NOT NULL,
  rev                           INT4 NOT NULL,
  revtype                       INT2,
  created_at                    TIMESTAMP,
  created_by                    TEXT,
  updated_at                    TIMESTAMP,
  updated_by                    TEXT,
  account_number                TEXT,
  client_id                     INT8,
  instantor_response_id         INT8,
  manual_weighted_wealthiness   NUMERIC(19, 2),
  months                        INT4,
  nordigen_log_id               INT8,
  nordigen_weighted_wealthiness NUMERIC(19, 2),
  period_from                   DATE,
  period_to                     DATE,
  PRIMARY KEY (id, rev)
);

CREATE TABLE alfa.wealthiness_category (
  id                            INT8           NOT NULL,
  created_at                    TIMESTAMP      NOT NULL,
  created_by                    TEXT,
  entity_version                INT8           NOT NULL,
  updated_at                    TIMESTAMP      NOT NULL,
  updated_by                    TEXT,
  category                      TEXT           NOT NULL,
  manual_weighted_wealthiness   NUMERIC(19, 2) NOT NULL,
  nordigen_categories           TEXT,
  nordigen_weighted_wealthiness NUMERIC(19, 2) NOT NULL,
  weight_in_precent             NUMERIC(19, 2) NOT NULL,
  wealthiness_id                INT8           NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE alfa.wealthiness_category_audit (
  id                            INT8 NOT NULL,
  rev                           INT4 NOT NULL,
  revtype                       INT2,
  created_at                    TIMESTAMP,
  created_by                    TEXT,
  updated_at                    TIMESTAMP,
  updated_by                    TEXT,
  category                      TEXT,
  manual_weighted_wealthiness   NUMERIC(19, 2),
  nordigen_categories           TEXT,
  nordigen_weighted_wealthiness NUMERIC(19, 2),
  weight_in_precent             NUMERIC(19, 2),
  wealthiness_id                INT8,
  PRIMARY KEY (id, rev)
);

ALTER TABLE alfa.wealthiness_audit
  ADD CONSTRAINT FKsncfkmtmhwghnggnt4hq4idx2
FOREIGN KEY (rev)
REFERENCES common.revision;


ALTER TABLE alfa.wealthiness_category_audit
  ADD CONSTRAINT FKexomjue7e2d337yl1vcytivgn
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE alfa.wealthiness_category ADD CONSTRAINT fk_wealthiness_category_wealthiness_id FOREIGN KEY (wealthiness_id) REFERENCES alfa.wealthiness (id) ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS idx_wealthiness_category_wealthiness_id ON alfa.wealthiness_category USING btree (wealthiness_id);
