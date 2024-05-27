SET client_encoding = 'UTF8';
SET search_path = presence, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE outbound_load
(
  id             BIGINT                   NOT NULL,
  entity_version BIGINT                   NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  updated_by     TEXT,
  load_id        BIGINT                   NOT NULL,
  service_id     BIGINT                   NOT NULL,
  status         TEXT                     NOT NULL,
  description    TEXT                     NOT NULL,
  added_at       TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE outbound_load_audit
(
  id          BIGINT  NOT NULL,
  rev         INTEGER NOT NULL,
  revtype     SMALLINT,
  created_at  TIMESTAMP WITH TIME ZONE,
  updated_at  TIMESTAMP WITH TIME ZONE,
  created_by  TEXT,
  updated_by  TEXT,
  load_id     BIGINT,
  service_id  BIGINT,
  status      TEXT,
  description TEXT,
  added_at    TIMESTAMP WITH TIME ZONE
);

CREATE TABLE outbound_load_record
(
  id                 BIGINT                   NOT NULL,
  entity_version     BIGINT                   NOT NULL,
  created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by         TEXT,
  updated_by         TEXT,
  source_id          INTEGER                  NOT NULL,
  name               TEXT                     NOT NULL,
  status             TEXT                     NOT NULL,
  qualification_code SMALLINT,
  outbound_load_id   BIGINT                   NOT NULL
);

CREATE TABLE outbound_load_record_audit
(
  id                 BIGINT  NOT NULL,
  rev                INTEGER NOT NULL,
  revtype            SMALLINT,
  created_at         TIMESTAMP WITH TIME ZONE,
  updated_at         TIMESTAMP WITH TIME ZONE,
  created_by         TEXT,
  updated_by         TEXT,
  source_id          INTEGER,
  name               TEXT,
  status             TEXT,
  qualification_code SMALLINT,
  outbound_load_id   BIGINT
);

CREATE TABLE phone_record
(
  id                      BIGINT                   NOT NULL,
  entity_version          BIGINT                   NOT NULL,
  created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by              TEXT,
  updated_by              TEXT,
  number                  TEXT                     NOT NULL,
  description             TEXT                     NOT NULL,
  outbound_load_record_id BIGINT                   NOT NULL
);

CREATE TABLE phone_record_audit
(
  id                      BIGINT  NOT NULL,
  rev                     INTEGER NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP WITH TIME ZONE,
  updated_at              TIMESTAMP WITH TIME ZONE,
  created_by              TEXT,
  updated_by              TEXT,
  number                  TEXT,
  description             TEXT,
  outbound_load_record_id BIGINT
);

ALTER TABLE ONLY outbound_load
  ADD CONSTRAINT outbound_load_pkey PRIMARY KEY (id),
  ADD UNIQUE (load_id, service_id);

ALTER TABLE ONLY outbound_load_record
  ADD CONSTRAINT outbound_load_record_pkey PRIMARY KEY (id),
  ADD CONSTRAINT fk_outbound_load_id FOREIGN KEY (outbound_load_id) REFERENCES outbound_load (id),
  ADD UNIQUE (source_id, outbound_load_id);

ALTER TABLE ONLY phone_record
  ADD CONSTRAINT phone_record_pkey PRIMARY KEY (id),
  ADD CONSTRAINT fk_outbound_load_record_id FOREIGN KEY (outbound_load_record_id) REFERENCES outbound_load_record (id);

ALTER TABLE ONLY outbound_load_audit
  ADD CONSTRAINT outbound_load_audit_revision FOREIGN KEY (rev) REFERENCES common.revision (id);

ALTER TABLE ONLY outbound_load_record_audit
  ADD CONSTRAINT outbound_load_record_audit_revision FOREIGN KEY (rev) REFERENCES common.revision (id);

ALTER TABLE ONLY phone_record_audit
  ADD CONSTRAINT phone_record_audit_revision FOREIGN KEY (rev) REFERENCES common.revision (id);

CREATE INDEX idx_phone_record_outbound_load_record_id
  ON phone_record USING btree(outbound_load_record_id);

CREATE SEQUENCE source_id_seq
  START WITH 1000
  INCREMENT BY 1
  MINVALUE 1000
  MAXVALUE 2147483647
  CYCLE
  CACHE 1
  OWNED BY outbound_load_record.source_id;
