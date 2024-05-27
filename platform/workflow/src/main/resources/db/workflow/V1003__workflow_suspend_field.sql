ALTER TABLE workflow.workflow
  ADD COLUMN suspended BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE workflow.workflow_audit
  ADD COLUMN suspended BOOLEAN;

UPDATE workflow.workflow
SET suspended = FALSE;



