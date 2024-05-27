
ALTER TABLE workflow.workflow_attribute 
  DROP CONSTRAINT fk_workflow_attribute_workflow_id,
  ADD CONSTRAINT fk_workflow_attribute_workflow_id 
    FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id) ON DELETE CASCADE;


ALTER TABLE workflow.activity 
  DROP CONSTRAINT fk_activity_workflow_id,
  ADD CONSTRAINT fk_activity_workflow_id 
    FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id) ON DELETE CASCADE;


