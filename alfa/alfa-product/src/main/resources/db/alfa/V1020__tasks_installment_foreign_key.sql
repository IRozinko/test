ALTER TABLE task.task
  ADD CONSTRAINT fk_task_installment_id FOREIGN KEY (installment_id) REFERENCES lending.installment(id);
