-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_task_created_at ON task.task (created_at);
CREATE INDEX IF NOT EXISTS idx_task_log_agent_stats ON task.log (created_at, agent, operation);

-- Improve task queue performance
CREATE INDEX IF NOT EXISTS idx_task_open_agent_tasks ON task.task (status, agent)
  WHERE status = 'OPEN';

-- NEW INDEXES
CREATE INDEX IF NOT EXISTS idx_task_task_type_trgm ON task.task USING gin (task_type gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_task_agent_trgm ON task.task USING gin (agent gin_trgm_ops);
