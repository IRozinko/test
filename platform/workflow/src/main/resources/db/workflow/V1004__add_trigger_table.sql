CREATE TABLE workflow.trigger (
  id              BIGINT                   NOT NULL,
  created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by      TEXT,
  updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by      TEXT,
  entity_version  BIGINT                   NOT NULL,
  workflow_id     BIGINT                   NOT NULL,
  activity_id     BIGINT                   NOT NULL,
  status          TEXT                     NOT NULL,
  name            TEXT                     NOT NULL,
  params          TEXT,
  error           TEXT,
  next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE ONLY workflow.trigger
  ADD CONSTRAINT trigger_pkey PRIMARY KEY (id);

ALTER TABLE workflow.trigger
  ADD CONSTRAINT trigger_workflow_fk FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id) ON DELETE CASCADE;

ALTER TABLE workflow.trigger
  ADD CONSTRAINT trigger_activity_fk FOREIGN KEY (activity_id) REFERENCES workflow.activity (id) ON DELETE CASCADE;
