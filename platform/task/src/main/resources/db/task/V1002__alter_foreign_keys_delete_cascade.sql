
ALTER TABLE task.log
  DROP CONSTRAINT fk_log_task_id,
  ADD CONSTRAINT fk_log_task_id
    FOREIGN KEY (task_id) REFERENCES task.task (id) ON DELETE CASCADE;
