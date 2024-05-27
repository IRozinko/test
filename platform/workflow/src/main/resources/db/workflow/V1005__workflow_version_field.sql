ALTER TABLE workflow.workflow
  ADD COLUMN version INTEGER;

ALTER TABLE workflow.workflow_audit
  ADD COLUMN version INTEGER;

UPDATE workflow.workflow
SET version = 0;

ALTER TABLE workflow.workflow
  ALTER COLUMN version SET NOT NULL;



