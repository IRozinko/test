ALTER TABLE task.task
    ADD COLUMN parent_task_id BIGINT,
    ADD CONSTRAINT fk_parent_task_id
        FOREIGN KEY (parent_task_id) REFERENCES task.task (id);

ALTER TABLE task.task_audit ADD COLUMN parent_task_id BIGINT;
