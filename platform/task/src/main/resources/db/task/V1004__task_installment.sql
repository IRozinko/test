ALTER TABLE task.task
  ADD COLUMN installment_id bigint;

ALTER TABLE task.task_audit
  ADD COLUMN installment_id bigint;
