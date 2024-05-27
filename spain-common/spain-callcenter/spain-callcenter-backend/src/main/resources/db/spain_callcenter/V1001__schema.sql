SET client_encoding = 'UTF8';
SET search_path = spain_callcenter, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE call
(
  id             BIGINT                   NOT NULL,
  entity_version BIGINT                   NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  updated_by     TEXT,
  provider_id    BIGINT                   NOT NULL,
  client_id      BIGINT                   NOT NULL,
  status         TEXT                     NOT NULL
);

CREATE TABLE call_audit
(
  id          BIGINT  NOT NULL,
  rev         INTEGER NOT NULL,
  revtype     SMALLINT,
  created_at  TIMESTAMP WITH TIME ZONE,
  updated_at  TIMESTAMP WITH TIME ZONE,
  created_by  TEXT,
  updated_by  TEXT,
  provider_id BIGINT,
  client_id   BIGINT,
  status      TEXT
);

ALTER TABLE ONLY call
  ADD CONSTRAINT call_pkey PRIMARY KEY (id),
  ADD CONSTRAINT fk_call_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id),
  ADD UNIQUE (client_id, provider_id);

ALTER TABLE ONLY call_audit
  ADD CONSTRAINT call_audit_revision FOREIGN KEY (rev) REFERENCES common.revision (id);
