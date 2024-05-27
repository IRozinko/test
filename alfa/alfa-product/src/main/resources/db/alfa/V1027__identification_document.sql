set search_path to alfa;

CREATE TABLE identification_document (
  id                          BIGINT                      NOT NULL,
  created_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by                  TEXT,
  entity_version              BIGINT                      NOT NULL,
  updated_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by                  TEXT,

  client_id                   BIGINT                      NOT NULL,
  document_type               TEXT                        NOT NULL,
  document_number             TEXT                        NOT NULL,
  surname_1                   TEXT,
  surname_2                   TEXT                        NOT NULL,
  name                        TEXT                        NOT NULL,
  gender                      TEXT,
  nationality                 TEXT                        NOT NULL,
  date_of_birth               DATE                        NOT NULL,
  expiration_date             DATE                        NOT NULL,
  full_address                TEXT                        NOT NULL,
  place_of_birth              TEXT,
  resident_since              DATE,

  front_file_id               BIGINT                      NOT NULL,
  front_file_name             TEXT                        NOT NULL,
  back_file_id                BIGINT                      NOT NULL,
  back_file_name              TEXT                        NOT NULL,

  customer_service_assessment TEXT
);

ALTER TABLE ONLY identification_document
  ADD CONSTRAINT id_doc_row_pkey PRIMARY KEY (id);

ALTER TABLE alfa.identification_document
  ADD CONSTRAINT fk_id_document_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);

CREATE INDEX IF NOT EXISTS idx_id_document_client_id
  ON alfa.identification_document
  USING btree (client_id);

CREATE INDEX IF NOT EXISTS idx_id_document_expiration_date
  ON alfa.identification_document
  USING btree (expiration_date);

ALTER TABLE alfa.identification_document
  ADD CONSTRAINT fk_id_document_front_file_id FOREIGN KEY (front_file_id) REFERENCES storage.cloud_file (id);
ALTER TABLE alfa.identification_document
  ADD CONSTRAINT fk_id_document_back_file_id FOREIGN KEY (back_file_id) REFERENCES storage.cloud_file (id);

CREATE TABLE identification_document_audit (
  id                          BIGINT  NOT NULL,
  rev                         INTEGER NOT NULL,
  revtype                     SMALLINT,
  created_at                  TIMESTAMP,
  created_by                  TEXT,
  updated_at                  TIMESTAMP,
  updated_by                  TEXT,

  client_id                   BIGINT,
  document_type               TEXT,
  document_number             TEXT,
  surname_1                   TEXT,
  surname_2                   TEXT,
  name                        TEXT,
  gender                      TEXT,
  nationality                 TEXT,
  date_of_birth               DATE,
  expiration_date             DATE,
  full_address                TEXT,
  place_of_birth              TEXT,
  resident_since              DATE,

  front_file_id               BIGINT,
  front_file_name             TEXT,
  back_file_id                BIGINT,
  back_file_name              TEXT,
  customer_service_assessment TEXT,

  PRIMARY KEY (id, rev)
);
