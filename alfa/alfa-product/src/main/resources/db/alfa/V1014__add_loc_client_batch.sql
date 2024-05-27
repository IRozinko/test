SET search_path = alfa;

CREATE TABLE loc_batch (
  id             BIGINT                      NOT NULL,
  created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version BIGINT                      NOT NULL,
  updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by     TEXT,

  client_id      BIGINT                      NOT NULL,
  batch_number   BIGINT                      NOT NULL,
  status         TEXT                        NOT NULL,

  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_loc_batch_client_id
  ON loc_batch (client_id);

CREATE TABLE loc_batch_audit (
  id           BIGINT  NOT NULL,
  rev          INTEGER NOT NULL,
  revtype      SMALLINT,
  created_at   TIMESTAMP,
  created_by   TEXT,
  updated_at   TIMESTAMP,
  updated_by   TEXT,

  client_id    BIGINT  NOT NULL,
  batch_number BIGINT  NOT NULL,
  amount       NUMERIC(19, 2),
  status       TEXT    NOT NULL,

  PRIMARY KEY (id, rev)
);

CREATE SEQUENCE IF NOT EXISTS loc_batch_sequence
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;
