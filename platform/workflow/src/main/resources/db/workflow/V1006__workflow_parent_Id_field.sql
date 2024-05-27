ALTER TABLE workflow.workflow
  ADD COLUMN parent_workflow_id INTEGER;

ALTER TABLE workflow.workflow_audit
  ADD COLUMN parent_workflow_id INTEGER;

ALTER TABLE workflow.workflow
 ADD CONSTRAINT fk_workflow_parent_workflow_id FOREIGN KEY (parent_workflow_id) REFERENCES workflow.workflow (id);

