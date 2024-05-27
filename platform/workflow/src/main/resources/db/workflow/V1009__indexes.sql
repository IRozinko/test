-- OLD INDEXES

-- Improve performance of workflow scheduler
CREATE INDEX IF NOT EXISTS idx_activity_active_status_system_actor ON workflow.activity (status, actor)
  WHERE status = 'ACTIVE' AND actor = 'SYSTEM';

-- create index to speed up filter
CREATE INDEX IF NOT EXISTS idx_workflow_activity_workflow_id_active ON workflow.activity (workflow_id) WHERE status = 'ACTIVE';
CREATE INDEX IF NOT EXISTS idx_workflow_trigger_workflow_id_waiting ON workflow.trigger (workflow_id) WHERE status = 'WAITING';
CREATE INDEX IF NOT EXISTS idx_workflow_activity_created_at ON workflow.activity (created_at);
CREATE INDEX IF NOT EXISTS idx_workflow_created_at ON workflow.workflow (created_at);

-- DUPLICATE INDEXES
DROP INDEX IF EXISTS workflow.idx_activity_workflow_id;
DROP INDEX IF EXISTS workflow.idx_scoring_value_workflow_id_key_uq;
DROP INDEX IF EXISTS workflow.idx_scoring_value_workflow_id_key;
DROP INDEX IF EXISTS workflow.idx_workflow_attribute_workflow_id;
