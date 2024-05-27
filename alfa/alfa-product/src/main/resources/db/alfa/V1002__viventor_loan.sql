SET search_path = alfa;

CREATE TABLE viventor_loan_data (
  id                      BIGINT                      NOT NULL,
  created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by              TEXT,
  entity_version          BIGINT                      NOT NULL,
  updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by              TEXT,

  loan_id                 BIGINT                      NOT NULL,
  viventor_loan_id        TEXT                        NOT NULL,
  viventor_loan_extension SMALLINT                    NOT NULL,
  loan_index              SMALLINT                    NOT NULL,
  status                  TEXT                        NOT NULL,
  status_detail           TEXT,
  viventor_loan           TEXT                        NOT NULL,
  principal               NUMERIC(19, 2)              NOT NULL,
  interest_rate           NUMERIC(5, 2)               NOT NULL,
  last_synced_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  start_date              DATE                        NOT NULL,
  maturity_date           DATE                        NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_viventor_loan_data_loan_id
  ON viventor_loan_data (loan_id);

CREATE UNIQUE INDEX idx_viventor_loan_data_viventor_loan_id
  ON viventor_loan_data (viventor_loan_id);

CREATE INDEX idx_viventor_loan_data_status
  ON viventor_loan_data (status);

CREATE TABLE viventor_loan_data_audit (
  id                      BIGINT                        NOT NULL,
  rev                     INTEGER                       NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP,
  created_by              TEXT,
  updated_at              TIMESTAMP,
  updated_by              TEXT,

  loan_id                 BIGINT,
  viventor_loan_id        TEXT,
  viventor_loan_extension SMALLINT,
  loan_index              SMALLINT,
  status                  TEXT,
  status_detail           TEXT,
  viventor_loan           TEXT,
  principal               NUMERIC(19, 2),
  interest_rate           NUMERIC(5, 2),
  last_synced_at          TIMESTAMP WITHOUT TIME ZONE,
  start_date              DATE,
  maturity_date           DATE,

  PRIMARY KEY (id, rev)
);
