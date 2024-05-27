ALTER TABLE workflow.workflow_attribute_audit
  DROP CONSTRAINT workflow_attribute_audit_pkey;

ALTER TABLE workflow.workflow_attribute_audit
  ALTER COLUMN value DROP NOT NULL;

ALTER TABLE workflow.workflow_attribute_audit
  ALTER COLUMN key DROP NOT NULL;

ALTER TABLE workflow.workflow_attribute_audit
  ALTER COLUMN workflow_id DROP NOT NULL;
