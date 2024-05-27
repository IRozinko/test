SET search_path = affiliate, pg_catalog;

CREATE TABLE affiliate_request (
  id             bigint                      NOT NULL,
  created_at     timestamp without time zone NOT NULL,
  created_by     text,
  entity_version bigint                      NOT NULL,
  updated_at     timestamp without time zone NOT NULL,
  updated_by     text,
  application_id bigint,
  client_id      bigint,
  request_type   text                        NOT NULL,
  request        jsonb                       not null default '{}' :: jsonb,
  response       jsonb                       not null default '{}' :: jsonb
);

CREATE TABLE affiliate_request_audit (
  id             bigint  NOT NULL,
  rev            integer NOT NULL,
  revtype        smallint,
  created_at     timestamp without time zone,
  created_by     text,
  updated_at     timestamp without time zone,
  updated_by     text,
  application_id bigint,
  client_id      bigint,
  request_type   text    NOT NULL,
  request        jsonb   not null default '{}' :: jsonb,
  response       jsonb   not null default '{}' :: jsonb
);

ALTER TABLE ONLY affiliate_request
  ADD CONSTRAINT affiliate_request_pkey PRIMARY KEY (id);

ALTER TABLE ONLY affiliate_request_audit
  ADD CONSTRAINT affiliate_request_audit_pkey PRIMARY KEY (id, rev);


CREATE INDEX idx_affiliate_request_client_id
  ON affiliate_request
  USING btree (client_id);

CREATE INDEX idx_affiliate_request_application_id
  ON affiliate_request
  USING btree (application_id);

ALTER TABLE ONLY affiliate_request_audit
  ADD CONSTRAINT fk_affiliate_request_audit_rev_id FOREIGN KEY (rev) REFERENCES common.revision (id);
