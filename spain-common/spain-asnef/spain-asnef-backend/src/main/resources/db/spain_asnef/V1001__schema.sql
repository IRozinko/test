SET search_path = spain_asnef;
SET default_tablespace = '';
SET default_with_oids = FALSE;

CREATE TABLE log (
  id                   BIGINT                   NOT NULL,
  entity_version       BIGINT                   NOT NULL,
  created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by           TEXT,
  updated_by           TEXT,
  type                 TEXT                     NOT NULL,
  status               TEXT                     NOT NULL,
  prepared_at          TIMESTAMP WITH TIME ZONE NOT NULL,
  exported_at          TIMESTAMP WITH TIME ZONE,
  response_received_at TIMESTAMP WITH TIME ZONE,
  outgoing_file_id     BIGINT                   NOT NULL,
  incoming_file_id     BIGINT
);

ALTER TABLE ONLY log
  ADD CONSTRAINT log_pkey PRIMARY KEY (id);

CREATE TABLE log_row (
  id             BIGINT                   NOT NULL,
  entity_version BIGINT                   NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  updated_by     TEXT,
  status         TEXT                     NOT NULL,
  client_id      BIGINT                   NOT NULL,
  loan_id        BIGINT                   NOT NULL,
  number         TEXT                     NOT NULL,
  outgoing_row   TEXT                     NOT NULL,
  incoming_row   TEXT,
  log_id         BIGINT                   NOT NULL
);

ALTER TABLE ONLY log_row
  ADD CONSTRAINT log_row_pkey PRIMARY KEY (id);

ALTER TABLE log_row
  ADD CONSTRAINT log_row_log_fk FOREIGN KEY (log_id) REFERENCES log;

CREATE INDEX log_prepared_at_idx
  ON log USING BTREE (prepared_at);

CREATE INDEX log_row_loan_id_idx
  ON log_row USING BTREE (loan_id);
