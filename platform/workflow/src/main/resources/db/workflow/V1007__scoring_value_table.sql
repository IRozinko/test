CREATE TABLE scoring_value
(
  id             bigint NOT NULL,
  created_at     timestamp without time zone NOT NULL,
  created_by     text,
  entity_version bigint NOT NULL,
  updated_at     timestamp without time zone NOT NULL,
  updated_by     text,

  workflow_id    bigint NOT NULL,
  key            text,
  value          text,
  type           text,
  PRIMARY KEY (id),
  UNIQUE (workflow_id, key)
);

CREATE TABLE scoring_value_audit
(
  id          bigint  NOT NULL,
  rev         integer NOT NULL,
  revtype     smallint,
  created_at  timestamp without time zone,
  created_by  text,
  updated_at  timestamp without time zone,
  updated_by  text,

  workflow_id bigint,
  key         text,
  value       text,
  type        text,
  PRIMARY KEY (id, rev)
);

ALTER TABLE workflow.scoring_value
  ADD CONSTRAINT scoring_value_workflow_fk FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id) ON DELETE CASCADE;

CREATE INDEX idx_scoring_value_workflow_id_key_uq
ON workflow.scoring_value (workflow_id);

CREATE UNIQUE INDEX idx_scoring_value_workflow_id_key
ON workflow.scoring_value (workflow_id, key);


